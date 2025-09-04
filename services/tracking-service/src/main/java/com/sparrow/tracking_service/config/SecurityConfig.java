package com.sparrow.tracking_service.config;

import com.sparrow.tracking_service.security.JwtAuthFilter;
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
                .requestMatchers("/api/tracking/health").permitAll()
                .requestMatchers("/actuator/**").permitAll()
                
                .requestMatchers(HttpMethod.POST, "/api/tracking/update").hasAnyRole("DRIVER", "ADMIN", "STAFF")
                .requestMatchers(HttpMethod.GET, "/api/tracking/**").authenticated()
                .requestMatchers(HttpMethod.GET, "/api/tracking/driver/**").hasAnyRole("DRIVER", "ADMIN", "STAFF")
                .requestMatchers(HttpMethod.GET, "/api/tracking/location").hasAnyRole("ADMIN", "STAFF")

                .anyRequest().authenticated()
        );

        http.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}

