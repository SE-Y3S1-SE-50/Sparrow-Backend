package com.sparrow.warehouse_service.config;

import com.sparrow.warehouse_service.security.JwtAuthFilter;
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
                .requestMatchers("/api/warehouse/health").permitAll()
                .requestMatchers("/api/warehouse/**").authenticated()
                .requestMatchers("/api/warehouse/health", "/api/warehouse/ping").permitAll()

                .requestMatchers(HttpMethod.GET, "/api/warehouses/**").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/warehouses/**").hasAnyRole("ADMIN","STAFF")
                .requestMatchers(HttpMethod.PUT,  "/api/warehouses/**").hasAnyRole("ADMIN","STAFF")
                .requestMatchers(HttpMethod.PATCH,"/api/warehouses/**").hasAnyRole("ADMIN","STAFF")

                .requestMatchers(HttpMethod.DELETE,"/api/warehouses/**").hasRole("ADMIN")

                .anyRequest().permitAll()
        );

        http.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
