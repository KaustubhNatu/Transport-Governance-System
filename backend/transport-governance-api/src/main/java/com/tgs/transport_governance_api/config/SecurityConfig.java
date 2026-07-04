package com.tgs.transport_governance_api.config;

import com.tgs.transport_governance_api.auth.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth

                        .requestMatchers(
                                "/auth/**"
                        ).permitAll()

                        .requestMatchers("/users/**")
                        .hasRole("SUPER_ADMIN")

                        .requestMatchers("/schools/**")
                        .hasAnyRole("SUPER_ADMIN", "SCHOOL_ADMIN")

                        .requestMatchers(HttpMethod.GET, "/schools/**")
                        .hasAnyRole("SUPER_ADMIN", "SCHOOL_ADMIN")

                        .requestMatchers(HttpMethod.PUT, "/schools/**")
                        .hasAnyRole("SUPER_ADMIN", "SCHOOL_ADMIN")

                        .requestMatchers(HttpMethod.DELETE, "/schools/**")
                        .hasRole("SUPER_ADMIN")

                        .requestMatchers(HttpMethod.POST, "/vendors/**")
                        .hasRole("SUPER_ADMIN")

                        .requestMatchers(HttpMethod.GET, "/vendors/**")
                        .hasAnyRole(
                                "SUPER_ADMIN",
                                "SCHOOL_ADMIN",
                                "VENDOR_ADMIN"
                        )

                        .requestMatchers(HttpMethod.POST, "/vehicles/**")
                        .hasAnyRole(
                                "SUPER_ADMIN",
                                "VENDOR_ADMIN"
                        )

                        .requestMatchers(HttpMethod.GET, "/vehicles/**")
                        .hasAnyRole(
                                "SUPER_ADMIN",
                                "SCHOOL_ADMIN",
                                "VENDOR_ADMIN"
                        )

                        .requestMatchers(HttpMethod.POST, "/drivers/**")
                        .hasAnyRole(
                                "SUPER_ADMIN",
                                "VENDOR_ADMIN"
                        )

                        .requestMatchers(HttpMethod.GET, "/drivers/**")
                        .hasAnyRole(
                                "SUPER_ADMIN",
                                "SCHOOL_ADMIN",
                                "VENDOR_ADMIN"
                        )

                        .requestMatchers(HttpMethod.POST, "/routes/**")
                        .hasAnyRole(
                                "SUPER_ADMIN",
                                "VENDOR_ADMIN"
                        )

                        .requestMatchers(HttpMethod.GET, "/routes/**")
                        .hasAnyRole(
                                "SUPER_ADMIN",
                                "SCHOOL_ADMIN",
                                "VENDOR_ADMIN"
                        )

                        .requestMatchers(HttpMethod.POST, "/students/**")
                        .hasAnyRole(
                                "SUPER_ADMIN",
                                "SCHOOL_ADMIN"
                        )

                        .requestMatchers(HttpMethod.GET, "/students/**")
                        .hasAnyRole(
                                "SUPER_ADMIN",
                                "SCHOOL_ADMIN"
                        )

                        .requestMatchers(HttpMethod.POST, "/parents/**")
                        .hasAnyRole(
                                "SUPER_ADMIN",
                                "SCHOOL_ADMIN"
                        )

                        .requestMatchers(HttpMethod.GET, "/parents/**")
                        .hasAnyRole(
                                "SUPER_ADMIN",
                                "SCHOOL_ADMIN"
                        )

                        .requestMatchers(HttpMethod.POST, "/student-transport/**")
                        .hasAnyRole(
                                "SUPER_ADMIN",
                                "SCHOOL_ADMIN"
                        )

                        .requestMatchers(HttpMethod.GET, "/student-transport/**")
                        .hasAnyRole(
                                "SUPER_ADMIN",
                                "SCHOOL_ADMIN"
                        )

                        .requestMatchers(HttpMethod.POST, "/invoices/**")
                        .hasAnyRole(
                                "SUPER_ADMIN",
                                "VENDOR_ADMIN"
                        )

                        .requestMatchers(HttpMethod.GET, "/invoices/**")
                        .hasAnyRole(
                                "SUPER_ADMIN",
                                "SCHOOL_ADMIN",
                                "VENDOR_ADMIN"
                        )

                        .requestMatchers(HttpMethod.POST, "/payments/**")
                        .hasAnyRole(
                                "SUPER_ADMIN",
                                "VENDOR_ADMIN"
                        )

                        .requestMatchers(HttpMethod.GET, "/payments/**")
                        .hasAnyRole(
                                "SUPER_ADMIN",
                                "SCHOOL_ADMIN",
                                "VENDOR_ADMIN"
                        )

                        .anyRequest()
                        .authenticated()
                )

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        ))

                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}