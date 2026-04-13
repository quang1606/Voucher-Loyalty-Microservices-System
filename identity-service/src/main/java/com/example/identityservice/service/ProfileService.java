package com.example.identityservice.service;

import com.example.identityservice.configuration.KeycloakProperties;
import com.example.identityservice.dto.request.ChangePasswordRequest;
import com.example.identityservice.dto.request.UpdateProfileRequest;
import com.example.identityservice.dto.response.ProfileResponse;
import com.example.identityservice.entity.User;
import com.example.identityservice.entity.enums.Role;
import com.example.common.BaseException;
import org.springframework.http.HttpStatus;
import com.example.identityservice.repository.CustomerRepository;
import com.example.identityservice.repository.MerchantRepository;
import com.example.identityservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final Keycloak keycloak;
    private final KeycloakProperties keycloakProps;
    private final UserRepository userRepository;
    private final MerchantRepository merchantRepository;
    private final CustomerRepository customerRepository;

    private UUID getCurrentUserId() {
        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return UUID.fromString(jwt.getSubject());
    }

    private UserResource keycloakUser(UUID userId) {
        return keycloak.realm(keycloakProps.getRealm()).users().get(userId.toString());
    }

    public ProfileResponse getProfile() {
        UUID userId = getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> BaseException.builder()
                        .httpStatus(HttpStatus.NOT_FOUND)
                        .description("User không tồn tại")
                        .build());
        UserRepresentation kcUser = keycloakUser(userId).toRepresentation();

        ProfileResponse.ProfileResponseBuilder builder = ProfileResponse.builder()
                .id(userId)
                .username(kcUser.getUsername())
                .email(kcUser.getEmail())
                .firstName(kcUser.getFirstName())
                .lastName(kcUser.getLastName());

        if (user.getRole() == Role.PARTNER) {
            merchantRepository.findByUserId(userId).ifPresent(m ->
                    builder.storeName(m.getStoreName())
                            .phone(m.getPhone())
                            .category(m.getCategory()));
        } else if (user.getRole() == Role.CUSTOMER) {
            customerRepository.findByUserId(userId).ifPresent(c ->
                    builder.balance(c.getBalance())
                            .tier(c.getTier())
                            .point(c.getPoint()));
        }
        return builder.build();
    }

    @Transactional
    public void updateProfile(UpdateProfileRequest request) {
        UUID userId = getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> BaseException.builder()
                        .httpStatus(HttpStatus.NOT_FOUND)
                        .description("User không tồn tại")
                        .build());

        UserResource kcRes = keycloakUser(userId);
        UserRepresentation kcUser = kcRes.toRepresentation();
        if (request.getFirstName() != null) kcUser.setFirstName(request.getFirstName());
        if (request.getLastName() != null) kcUser.setLastName(request.getLastName());
        if (request.getEmail() != null) kcUser.setEmail(request.getEmail());
        kcRes.update(kcUser);

        if (request.getFirstName() != null) user.setFirstName(request.getFirstName());
        if (request.getLastName() != null) user.setLastName(request.getLastName());
        if (request.getEmail() != null) user.setEmail(request.getEmail());
        userRepository.save(user);

        if (user.getRole() == Role.PARTNER) {
            merchantRepository.findByUserId(userId).ifPresent(m -> {
                if (request.getPhone() != null) m.setPhone(request.getPhone());
                if (request.getStoreName() != null) m.setStoreName(request.getStoreName());
                merchantRepository.save(m);
            });
        }
    }

    public void changePassword(ChangePasswordRequest request) {
        UUID userId = getCurrentUserId();
        CredentialRepresentation cred = new CredentialRepresentation();
        cred.setType(CredentialRepresentation.PASSWORD);
        cred.setValue(request.getNewPassword());
        cred.setTemporary(false);
        keycloakUser(userId).resetPassword(cred);
    }
}
