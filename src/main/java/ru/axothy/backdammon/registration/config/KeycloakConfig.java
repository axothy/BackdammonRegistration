package ru.axothy.backdammon.registration.config;

import lombok.Getter;
import lombok.Setter;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "keycloak")
@Getter
@Setter
public class KeycloakConfig {
    private String resource;
    private String authServerUrl;
    private String credentialsSecret;
    private String realm;
    private String credentialsSecretRealm;
    private AdminCredentials adminCredentials;


    @Bean
    public Keycloak getKeycloak() {
        return KeycloakBuilder.builder().serverUrl(authServerUrl)
                .grantType("password")
                .realm("master")
                .clientId(resource)
                .clientSecret(credentialsSecret)
                .username(adminCredentials.getUsername())
                .password(adminCredentials.getPassword())
                .build();
    }

}
