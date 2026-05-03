package com.example.identityservice.service;

import com.example.identityservice.configuration.KeycloakProperties;
import com.example.identityservice.dto.request.ChangePasswordRequest;
import com.example.identityservice.dto.request.UpdateProfileRequest;
import com.example.identityservice.dto.response.ProfileResponse;
import com.example.identityservice.entity.Partner;
import com.example.identityservice.entity.User;
import com.example.identityservice.constant.Role;
import com.example.common.BaseErrorCode;
import com.example.common.BaseException;
import org.springframework.http.HttpStatus;
import com.example.identityservice.repository.PartnerRepository;
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
    private final PartnerRepository partnerRepository;

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
                        .errorCode(BaseErrorCode.NOT_FOUND.getErrorCode())
                        .description("User không tồn tại")
                        .build());
        UserRepresentation kcUser = keycloakUser(userId).toRepresentation();

        ProfileResponse.ProfileResponseBuilder builder = ProfileResponse.builder()
                .id(userId)
                .username(kcUser.getUsername())
                .email(kcUser.getEmail())
                .firstName(kcUser.getFirstName())
                .lastName(kcUser.getLastName())
                .role(user.getRole());

        if (user.getRole() == Role.PARTNER) {
            partnerRepository.findByUserId(userId).ifPresent(m ->
                    builder.storeName(m.getStoreName())
                            .phone(m.getPhone())
                            .category(m.getCategory()));
        }
        return builder.build();
    }

    @Transactional
    public void updateProfile(UpdateProfileRequest request) {
        UUID userId = getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> BaseException.builder()
                        .httpStatus(HttpStatus.NOT_FOUND)
                        .errorCode(BaseErrorCode.NOT_FOUND.getErrorCode())
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
            partnerRepository.findByUserId(userId).ifPresent(m -> {
                if (request.getPhone() != null) m.setPhone(request.getPhone());
                if (request.getStoreName() != null) m.setStoreName(request.getStoreName());
                partnerRepository.save(m);
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

  public String getNameStore(String partnerId) {
       Partner partner = partnerRepository.findByUserId(UUID.fromString(partnerId)).orElseThrow(() ->
              BaseException.builder()
                      .httpStatus(HttpStatus.NOT_FOUND)
                      .errorCode(BaseErrorCode.NOT_FOUND.getErrorCode())
                      .description("Partner không tồn tại")
                      .build());
       return  partner.getStoreName();
  }

  public boolean existsByStoreName(String storeName) {
      return partnerRepository.existsByStoreName(storeName);
  }

  public com.example.identityservice.entity.Partner getPartnerByUserId(String userId) {
      return partnerRepository.findByUserId(UUID.fromString(userId))
          .orElseThrow(() -> BaseException.builder()
              .httpStatus(HttpStatus.NOT_FOUND)
              .errorCode(BaseErrorCode.NOT_FOUND.getErrorCode())
              .description("Partner không tồn tại với userId: " + userId)
              .build());
  }

  public Partner getPartnerByStoreName(String storeName) {
      return partnerRepository.findByStoreName(storeName)
          .orElseThrow(() -> BaseException.builder()
              .httpStatus(HttpStatus.NOT_FOUND)
              .errorCode(BaseErrorCode.NOT_FOUND.getErrorCode())
              .description("Partner không tồn tại với storeName: " + storeName)
              .build());
  }
}
