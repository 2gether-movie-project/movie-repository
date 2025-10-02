package com.movieproject.common.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.http.HttpMethod.*;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/users/signup", "/api/users/login", "/api/users/refresh").permitAll()

                        //배우
                        .requestMatchers(POST, "/api/actors").hasRole("ADMIN")
                        .requestMatchers(PUT, "/api/actors/*").hasRole("ADMIN")
                        .requestMatchers(DELETE, "/api/actors/*").hasRole("ADMIN")

                        //디렉터
                        .requestMatchers(POST, "/api/directors").hasRole("ADMIN")
                        .requestMatchers(PUT, "/api/directors/*").hasRole("ADMIN")
                        .requestMatchers(DELETE, "/api/directors/*").hasRole("ADMIN")

                        //영화
                        .requestMatchers(POST, "/api/movies").hasRole("ADMIN")
                        .requestMatchers(PUT, "/api/movies/*").hasRole("ADMIN")
                        .requestMatchers(DELETE, "/api/movies/*").hasRole("ADMIN")

                        //출연진
                        .requestMatchers(POST, "/api/movies/cast").hasRole("ADMIN")
                        .requestMatchers(DELETE, "/api/movies/casts/*/*").hasRole("ADMIN")
                        .requestMatchers("/api/users/**").authenticated()
                        .anyRequest().permitAll()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}