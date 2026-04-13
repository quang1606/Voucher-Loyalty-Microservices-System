package com.example.identityservice.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "keycloak")
public class KeycloakProperties {
    private String serverUrl;
    private String realm;
    private String clientId;
    private String clientSecret;
    private String adminUsername;
    private String adminPassword;
    private String tokenUrl;

    public String getTokenUrl() {
        if (tokenUrl != null && !tokenUrl.isEmpty()) {
            return tokenUrl;
        }
        return serverUrl + "/realms/" + realm + "/protocol/openid-connect/token";
    }
}
