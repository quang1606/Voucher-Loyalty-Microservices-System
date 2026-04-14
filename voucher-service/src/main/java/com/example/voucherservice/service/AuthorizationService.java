package com.example.voucherservice.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Service
public class AuthorizationService {

    public Set<String> getAuthorities() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return getAuthorities(auth);
    }

    public Set<String> getAuthorities(Authentication authentication) {
        Set<String> out = new HashSet<>();
        if (authentication == null) {
            return out;
        }
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        if (authorities == null) {
            return out;
        }
        for (GrantedAuthority ga : authorities) {
            if (ga == null) {
                continue;
            }
            String a = ga.getAuthority();
            if (a != null) {
                out.add(a);
            }
        }
        return out;
    }

    public boolean isAdmin() {
        return isAdmin(SecurityContextHolder.getContext().getAuthentication());
    }

    public boolean isAdmin(Authentication authentication) {
        if (authentication == null) {
            return false;
        }
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        if (authorities == null) {
            return false;
        }
        for (GrantedAuthority ga : authorities) {
            if (ga == null) {
                continue;
            }
            String a = ga.getAuthority();
            if (a == null) {
                continue;
            }
            if (a.equalsIgnoreCase("ROLE_ADMIN") || a.equalsIgnoreCase("ADMIN")) {
                return true;
            }
        }
        return false;
    }

    public boolean hasRole(String role) {
        if (role == null) {
            return false;
        }
        String normalized = role.startsWith("ROLE_") ? role : "ROLE_" + role;
        return getAuthorities().stream()
                .anyMatch(a -> a.equalsIgnoreCase(normalized) || a.equalsIgnoreCase(role));
    }

    public boolean isCheckerRole() {
        return hasRole("CHECKER");
    }

    public String getName() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return getName(auth);
    }

    public String getName(Authentication authentication) {
        if (authentication == null) {
            return null;
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof Jwt jwt) {
            try {
                String name = jwt.getClaimAsString("preferred_username");
                if (name != null && !name.isBlank()) {
                    return name;
                }
            } catch (Exception ignored) {
            }
        }
        return authentication.getName();
    }

}
