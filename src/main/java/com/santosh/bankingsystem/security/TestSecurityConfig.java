package com.santosh.bankingsystem.security;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@TestConfiguration
@EnableWebSecurity
public class TestSecurityConfig {
    @Bean
    public SecurityFilterChain testSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher(new AntPathRequestMatcher("/**")) // match all URLs
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()  // Allow all for testing
                )
                .csrf(csrf -> csrf.disable()); // Disable CSRF for testing

        return http.build();
    }
}