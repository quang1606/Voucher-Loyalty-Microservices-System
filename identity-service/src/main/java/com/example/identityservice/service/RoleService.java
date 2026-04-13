package com.example.identityservice.service;

import com.example.identityservice.configuration.KeycloakProperties;
import com.example.identityservice.dto.request.CreateRoleRequest;
import com.example.identityservice.dto.request.UpdateRoleAttributesRequest;
import com.example.identityservice.dto.request.UpdateRoleRequest;
import com.example.identityservice.dto.response.RoleDetailResponse;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RolesResource;
import org.keycloak.representations.idm.RoleRepresentation;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleService {

    private static final Set<String> EXCLUDED_ROLES = Set.of("uma_authorization", "offline_access");

    private final Keycloak keycloak;
    private final KeycloakProperties keycloakProps;

    private RolesResource rolesResource() {
        return keycloak.realm(keycloakProps.getRealm()).roles();
    }

    public List<RoleDetailResponse> getAllRoles() {
        return rolesResource().list(false).stream()
                .filter(r -> !EXCLUDED_ROLES.contains(r.getName()) && !r.getName().startsWith("default-roles-"))
                .map(this::toDetailResponse)
                .collect(Collectors.toList());
    }

    public RoleDetailResponse getRole(String roleName) {
        return toDetailResponse(rolesResource().get(roleName).toRepresentation());
    }

    public void createRole(CreateRoleRequest request) {
        RoleRepresentation role = new RoleRepresentation();
        role.setName(request.getName());
        role.setDescription(request.getDescription());
        if (request.getAttributes() != null) {
            role.setAttributes(request.getAttributes());
        }
        rolesResource().create(role);
    }

    public void updateRole(String roleName, UpdateRoleRequest request) {
        RoleRepresentation role = rolesResource().get(roleName).toRepresentation();
        if (request.getDescription() != null) {
            role.setDescription(request.getDescription());
        }
        rolesResource().get(roleName).update(role);
    }

    public void deleteRole(String roleName) {
        rolesResource().deleteRole(roleName);
    }

    public void updateRoleAttributes(String roleName, UpdateRoleAttributesRequest request) {
        RoleRepresentation role = rolesResource().get(roleName).toRepresentation();
        if (role.getAttributes() == null) {
            role.setAttributes(new HashMap<>());
        }
        role.getAttributes().put("allowed-pages", request.getAllowedPages());
        rolesResource().get(roleName).update(role);
    }

    private RoleDetailResponse toDetailResponse(RoleRepresentation r) {
        return RoleDetailResponse.builder()
                .id(r.getId())
                .name(r.getName())
                .description(r.getDescription())
                .attributes(r.getAttributes())
                .build();
    }
}
