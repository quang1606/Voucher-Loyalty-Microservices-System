package com.example.identityservice.service;

import com.example.identityservice.configuration.KeycloakProperties;
import com.example.identityservice.dto.request.AllowedPagesRequest;
import com.example.identityservice.dto.request.LoginRequest;
import com.example.identityservice.dto.request.RefreshTokenRequest;
import com.example.identityservice.dto.request.RegisterCustomerRequest;
import com.example.identityservice.dto.response.AllowedPagesResponse;
import com.example.identityservice.dto.response.CreateUserResponse;
import com.example.identityservice.dto.response.LoginResponse;
import com.example.identityservice.entity.Customer;
import com.example.identityservice.entity.User;
import com.example.identityservice.constant.Role;
import com.example.common.BaseErrorCode;
import com.example.common.BaseException;
import org.springframework.http.HttpStatus;
import com.example.identityservice.repository.CustomerRepository;
import com.example.identityservice.repository.UserRepository;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final KeycloakProperties keycloakProps;
    private final Keycloak keycloak;
    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    private UsersResource usersResource() {
        return keycloak.realm(keycloakProps.getRealm()).users();
    }

    public LoginResponse login(LoginRequest request) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "password");
        form.add("client_id", keycloakProps.getClientId());
        if (keycloakProps.getClientSecret() != null && !keycloakProps.getClientSecret().isEmpty()) {
            form.add("client_secret", keycloakProps.getClientSecret());
        }
        form.add("username", request.getUsername());
        form.add("password", request.getPassword());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                    keycloakProps.getTokenUrl(),
                    HttpMethod.POST,
                    new HttpEntity<>(form, headers),
                    Map.class
            );
            Map<String, Object> body = response.getBody();
            return LoginResponse.builder()
                    .accessToken((String) body.get("access_token"))
                    .refreshToken((String) body.get("refresh_token"))
                    .expiresIn(((Number) body.get("expires_in")).longValue())
                    .tokenType((String) body.get("token_type"))
                    .build();
        } catch (Exception e) {
            throw BaseException.builder()
                    .httpStatus(HttpStatus.UNAUTHORIZED)
                    .errorCode(BaseErrorCode.UNAUTHORIZED.getErrorCode())
                    .description("Đăng nhập thất bại: " + e.getMessage())
                    .build();
        }
    }

    public LoginResponse refreshToken(RefreshTokenRequest request) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "refresh_token");
        form.add("client_id", keycloakProps.getClientId());
        if (keycloakProps.getClientSecret() != null && !keycloakProps.getClientSecret().isEmpty()) {
            form.add("client_secret", keycloakProps.getClientSecret());
        }
        form.add("refresh_token", request.getRefreshToken());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                    keycloakProps.getTokenUrl(),
                    HttpMethod.POST,
                    new HttpEntity<>(form, headers),
                    Map.class
            );
            Map<String, Object> body = response.getBody();
            return LoginResponse.builder()
                    .accessToken((String) body.get("access_token"))
                    .refreshToken((String) body.get("refresh_token"))
                    .expiresIn(((Number) body.get("expires_in")).longValue())
                    .tokenType((String) body.get("token_type"))
                    .build();
        } catch (Exception e) {
            throw BaseException.builder()
                    .httpStatus(HttpStatus.UNAUTHORIZED)
                    .errorCode(BaseErrorCode.UNAUTHORIZED.getErrorCode())
                    .description("Refresh token thất bại: " + e.getMessage())
                    .build();
        }
    }

    @Transactional
    public CreateUserResponse registerCustomer(RegisterCustomerRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw BaseException.builder()
                    .httpStatus(HttpStatus.CONFLICT)
                    .errorCode(BaseErrorCode.CONFLICT.getErrorCode())
                    .description("Email đã tồn tại: " + request.getEmail())
                    .build();
        }

        UserRepresentation kcUser = new UserRepresentation();
        kcUser.setUsername(request.getUsername());
        kcUser.setEmail(request.getEmail());
        kcUser.setFirstName(request.getFirstName());
        kcUser.setLastName(request.getLastName());
        kcUser.setEnabled(true);

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
                    .description("Đăng ký thất bại: " + response.getStatusInfo())
                    .build();
        }

        String locationHeader = response.getHeaderString("Location");
        String kcId = locationHeader.substring(locationHeader.lastIndexOf("/") + 1);
        UUID userId = UUID.fromString(kcId);

        try {
            User user = new User();
            user.setUserId(userId);
            user.setUsername(request.getUsername());
            user.setEmail(request.getEmail());
            user.setFirstName(request.getFirstName());
            user.setLastName(request.getLastName());
            user.setRole(Role.CUSTOMER);
            userRepository.save(user);

            Customer customer = new Customer();
            customer.setUserId(userId);
            customerRepository.save(customer);
        } catch (Exception e) {
            log.error("DB save failed, rolling back Keycloak user: {}", kcId, e);
            usersResource().delete(kcId);
            throw BaseException.builder()
                    .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                    .errorCode(BaseErrorCode.INTERNAL_ERROR.getErrorCode())
                    .description("Đăng ký thất bại, đã rollback: " + e.getMessage())
                    .build();
        }

        try {
            RoleRepresentation roleRep = keycloak.realm(keycloakProps.getRealm())
                    .roles().get(Role.CUSTOMER.name()).toRepresentation();
            usersResource().get(kcId).roles().realmLevel().add(List.of(roleRep));
        } catch (Exception e) {
            log.warn("Assign role CUSTOMER thất bại cho user {}: {}", kcId, e.getMessage());
        }

        return CreateUserResponse.builder().id(userId).build();
    }

    public AllowedPagesResponse getAllowedPages(AllowedPagesRequest request) {
        List<RoleRepresentation> allRoles = keycloak.realm(keycloakProps.getRealm())
                .roles().list(false);

        Set<String> inputRoles = new HashSet<>(request.getRoles());
        Set<String> pages = new LinkedHashSet<>();

        for (RoleRepresentation role : allRoles) {
            if (inputRoles.contains(role.getName()) && role.getAttributes() != null) {
                List<String> allowedPages = role.getAttributes().get("allowed-pages");
                if (allowedPages != null) {
                    pages.addAll(allowedPages);
                }
            }
        }
        return AllowedPagesResponse.builder()
                .allowedPages(new ArrayList<>(pages))
                .build();
    }
}
