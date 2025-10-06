package com.hub.order_service.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.Optional;

@Slf4j
@Configuration
@EntityScan("com.hub.course_service.model")
@EnableJpaRepositories("com.hub.course_service.repository")
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class DatabaseAutoConfig {

    private static final String FIRST_NAME = "given_name";
    private static final String LAST_NAME = "family_name";

    @Bean
    public AuditorAware<String> auditorAware() {
        return () -> {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();

            if (auth == null)
                return Optional.of("system");

            /*
                log.info("Auth class: " + auth.getClass());
                Auth class: class org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
                log.info("Principal class: " + auth.getPrincipal().getClass());
                Principal class: class org.springframework.security.oauth2.jwt.Jwt
            */

            if (auth instanceof JwtAuthenticationToken jwtAuthenticationToken) {
                Jwt jwt = (Jwt) auth.getPrincipal();
                String fullName = jwt.getClaimAsString(FIRST_NAME) + " " + jwt.getClaimAsString(LAST_NAME);
                return Optional.of(fullName);
            }

            return Optional.of(auth.getName()); // Returns the id of this principal.

        };
    }


}
