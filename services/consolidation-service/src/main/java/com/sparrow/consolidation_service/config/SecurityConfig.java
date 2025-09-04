package com.sparrow.consolidation_service.config;

import com.sparrow.consolidation_service.security.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.http.HttpMethod;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable());

        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/consolidation/health").permitAll()
                .requestMatchers("/actuator/**").permitAll()
                
                .requestMatchers(HttpMethod.POST, "/api/consolidation/create").hasAnyRole("ADMIN", "STAFF")
                .requestMatchers(HttpMethod.POST, "/api/consolidation/auto-consolidate/**").hasAnyRole("ADMIN", "STAFF")
                .requestMatchers(HttpMethod.GET, "/api/consolidation/**").authenticated()
                .requestMatchers(HttpMethod.PUT, "/api/consolidation/**/status/**").hasAnyRole("ADMIN", "STAFF", "DRIVER")

                .anyRequest().authenticated()
        );

        http.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}

