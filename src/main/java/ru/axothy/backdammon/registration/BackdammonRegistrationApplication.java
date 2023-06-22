package ru.axothy.backdammon.registration;

import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@RefreshScope
public class BackdammonRegistrationApplication {
	public static void main(String[] args) {
		SpringApplication.run(BackdammonRegistrationApplication.class, args);
	}


	@Bean
	public RestTemplate getRestTemplate() {
		return new RestTemplate();
	}

}

