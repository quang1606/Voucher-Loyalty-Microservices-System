package com.example.identityservice.service;

import com.example.identityservice.configuration.KeycloakProperties;
import com.example.identityservice.dto.request.CreateUserRequest;
import com.example.identityservice.dto.request.ResetPasswordRequest;
import com.example.identityservice.dto.request.UpdateUserRequest;
import com.example.identityservice.dto.response.CreateUserResponse;
import com.example.identityservice.dto.response.SystemUserResponse;
import com.example.identityservice.entity.Customer;
import com.example.identityservice.entity.Partner;
import com.example.identityservice.entity.User;
import com.example.identityservice.constant.Role;
import com.example.common.BaseErrorCode;
import com.example.common.BaseException;
import org.springframework.http.HttpStatus;
import com.example.identityservice.repository.CustomerRepository;
import com.example.identityservice.repository.MerchantRepository;
import com.example.identityservice.repository.UserRepository;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SystemUserService {

    private final Keycloak keycloak;
    private final KeycloakProperties keycloakProps;
    private final UserRepository userRepository;
    private final MerchantRepository merchantRepository;
    private final CustomerRepository customerRepository;

    private UsersResource usersResource() {
        return keycloak.realm(keycloakProps.getRealm()).users();
    }

    public List<SystemUserResponse> getAllUsers() {
        List<User> dbUsers = userRepository.findAll();
        Map<UUID, Partner> merchants = merchantRepository.findAll().stream()
                .collect(Collectors.toMap(Partner::getUserId, m -> m));
        Map<UUID, Customer> customers = customerRepository.findAll().stream()
                .collect(Collectors.toMap(Customer::getUserId, c -> c));

        return dbUsers.stream().map(user -> {
            UUID uid = user.getUserId();
            SystemUserResponse.SystemUserResponseBuilder builder = SystemUserResponse.builder()
                    .id(uid)
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName());

            try {
                UserRepresentation kcUser = usersResource().get(uid.toString()).toRepresentation();
                builder.enabled(kcUser.isEnabled());
            } catch (Exception e) {
                log.warn("Không lấy được Keycloak info cho user {}: {}", uid, e.getMessage());
            }

            Partner m = merchants.get(uid);
            if (m != null) {
                builder.storeName(m.getStoreName())
                        .phone(m.getPhone())
                        .category(m.getCategory())
                        .status(m.getStatus());
            }
            Customer c = customers.get(uid);
            if (c != null) {
                builder.balance(c.getBalance())
                        .tier(c.getTier())
                        .point(c.getPoint());
            }
            return builder.build();
        }).collect(Collectors.toList());
    }

    @Transactional
    public CreateUserResponse createUser(CreateUserRequest request) {
        validateCreateUserRequest(request);

        // 1. Tạo user trên Keycloak
        UserRepresentation kcUser = new UserRepresentation();
        kcUser.setUsername(request.getUsername());
        kcUser.setEmail(request.getEmail());
        kcUser.setFirstName(request.getFirstName());
        kcUser.setLastName(request.getLastName());
        kcUser.setEnabled(request.getEnabled());

        CredentialRepresentation cred = new CredentialRepresentation();
        cred.setType(CredentialRepresentation.PASSWORD);
        cred.setValue(request.getPassword());
        cred.setTemporary(false);
        kcUser.setCredentials(List.of(cred));

        Response response = usersResource().create(kcUser);
        if (response.getStatus() != 201) {
            throw BaseException.builder()
                    .httpStatus(HttpStatus.BAD_REQUEST)
                    .errorCode(BaseErrorCode.BAD_REQUEST.getErrorCode())
                    .description("Tạo user trên Keycloak thất bại: " + response.getStatusInfo())
                    .build();
        }

        String locationHeader = response.getHeaderString("Location");
        String kcId = locationHeader.substring(locationHeader.lastIndexOf("/") + 1);
        UUID userId = UUID.fromString(kcId);

        try {
            RoleRepresentation roleRep = keycloak.realm(keycloakProps.getRealm())
                    .roles().get(request.getRole().name()).toRepresentation();
            usersResource().get(kcId).roles().realmLevel().add(List.of(roleRep));
        } catch (Exception e) {
            log.error("Assign role thất bại, rolling back Keycloak user: {}", kcId, e);
            usersResource().delete(kcId);
            throw BaseException.builder()
                    .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                    .errorCode(BaseErrorCode.INTERNAL_ERROR.getErrorCode())
                    .description("Assign role thất bại, đã rollback: " + e.getMessage())
                    .build();
        }

        try {
            User user = new User();
            user.setUserId(userId);
            user.setUsername(request.getUsername());
            user.setEmail(request.getEmail());
            user.setFirstName(request.getFirstName());
            user.setLastName(request.getLastName());
            user.setRole(request.getRole());
            userRepository.save(user);

            if (request.getRole() == Role.PARTNER) {
                Partner merchant = new Partner();
                merchant.setUserId(userId);
                merchant.setStoreName(request.getStoreName());
                merchant.setPhone(request.getPhone());
                merchant.setCategory(request.getCategory());
                merchantRepository.save(merchant);
            }
        } catch (Exception e) {
            log.error("DB save failed, rolling back Keycloak user: {}", kcId, e);
            usersResource().delete(kcId);
            throw BaseException.builder()
                    .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                    .errorCode(BaseErrorCode.INTERNAL_ERROR.getErrorCode())
                    .description("Tạo user thất bại, đã rollback: " + e.getMessage())
                    .build();
        }

        return CreateUserResponse.builder().id(userId).build();
    }

  private void validateCreateUserRequest(CreateUserRequest request) {
    if (userRepository.existsByEmail(request.getEmail())) {
      throw BaseException.builder()
          .httpStatus(HttpStatus.CONFLICT)
          .errorCode(BaseErrorCode.CONFLICT.getErrorCode())
          .description("Email đã tồn tại: " + request.getEmail())
          .build();
    }
    if (request.getPhone() != null && merchantRepository.existsByPhone(request.getPhone())) {
      throw BaseException.builder()
          .httpStatus(HttpStatus.CONFLICT)
          .errorCode(BaseErrorCode.CONFLICT.getErrorCode())
          .description("Số điện thoại đã tồn tại: " + request.getPhone())
          .build();
    }
    if(request.getStoreName() !=null && merchantRepository.existsByStoreName(request.getStoreName())){
      throw BaseException.builder()
          .httpStatus(HttpStatus.CONFLICT)
          .errorCode(BaseErrorCode.CONFLICT.getErrorCode())
          .description("Tên đối tác đã tồn tại: " + request.getPhone())
          .build();
    }
  }

  @Transactional
    public void updateUser(UUID id, UpdateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> BaseException.builder()
                        .httpStatus(HttpStatus.NOT_FOUND)
                        .errorCode(BaseErrorCode.NOT_FOUND.getErrorCode())
                        .description("User không tồn tại: " + id)
                        .build());

        // Update Keycloak
        UserResource kcUserResource = usersResource().get(id.toString());
        UserRepresentation kcUser = kcUserResource.toRepresentation();
        if (request.getEmail() != null) kcUser.setEmail(request.getEmail());
        if (request.getFirstName() != null) kcUser.setFirstName(request.getFirstName());
        if (request.getLastName() != null) kcUser.setLastName(request.getLastName());
        if (request.getEnabled() != null) kcUser.setEnabled(request.getEnabled());
        kcUserResource.update(kcUser);

        // Update DB
        if (request.getEmail() != null) user.setEmail(request.getEmail());
        if (request.getFirstName() != null) user.setFirstName(request.getFirstName());
        if (request.getLastName() != null) user.setLastName(request.getLastName());
        userRepository.save(user);

        if (user.getRole() == Role.PARTNER) {
            merchantRepository.findByUserId(id).ifPresent(m -> {
                if (request.getStoreName() != null) m.setStoreName(request.getStoreName());
                if (request.getPhone() != null) m.setPhone(request.getPhone());
                if (request.getCategory() != null) m.setCategory(request.getCategory());
                if (request.getStatus() != null) m.setStatus(request.getStatus());
                merchantRepository.save(m);
            });
        }
    }

    @Transactional
    public void deleteUser(UUID id) {
        userRepository.findById(id)
                .orElseThrow(() -> BaseException.builder()
                        .httpStatus(HttpStatus.NOT_FOUND)
                        .errorCode(BaseErrorCode.NOT_FOUND.getErrorCode())
                        .description("User không tồn tại: " + id)
                        .build());
        usersResource().delete(id.toString());
        merchantRepository.deleteByUserId(id);
        customerRepository.deleteByUserId(id);
        userRepository.deleteById(id);
    }

    public void resetPassword(UUID id, ResetPasswordRequest request) {
        userRepository.findById(id)
                .orElseThrow(() -> BaseException.builder()
                        .httpStatus(HttpStatus.NOT_FOUND)
                        .errorCode(BaseErrorCode.NOT_FOUND.getErrorCode())
                        .description("User không tồn tại: " + id)
                        .build());
        CredentialRepresentation cred = new CredentialRepresentation();
        cred.setType(CredentialRepresentation.PASSWORD);
        cred.setValue(request.getPassword());
        cred.setTemporary(false);
        usersResource().get(id.toString()).resetPassword(cred);
    }
}
