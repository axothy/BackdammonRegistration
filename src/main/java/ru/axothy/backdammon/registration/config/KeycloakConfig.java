package ru.axothy.backdammon.registration.config;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KeycloakConfig {
    @Value("${keycloak.resource}")
    private String keycloakResource;

    @Value("${keycloak.credentials.secret}")
    private String keycloakCredentialsSecret;

    @Value("${keycloak.realm}")
    private String keycloakRealm;

    @Value("${keycloak.auth-server-url}")
    private String keycloakUrl;

    @Value("${admin.credentials.username}")
    private String keycloakAdminUsername;

    @Value("${admin.credentials.password}")
    private String keycloakAdminPassword;

    @Bean
    public Keycloak getKeycloak() {
        return KeycloakBuilder.builder().serverUrl(keycloakUrl)
                .grantType("password")
                .realm(keycloakRealm)
                .clientId(keycloakResource)
                .clientSecret(keycloakCredentialsSecret)
                .username(keycloakAdminUsername)
                .password(keycloakAdminPassword)
                .build();
    }
}
