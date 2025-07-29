package com.example.demo.config;

import com.example.demo.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(withDefaults()) // Habilita CORS con la configuración de abajo
                .csrf(csrf ->
                        csrf
                                .disable())
                .authorizeHttpRequests(authRequest ->
                        authRequest
                                .requestMatchers("/auth/**").permitAll()
                                .requestMatchers("/uploads/**").permitAll()
                                .requestMatchers("/ws/**", "/topic/**").permitAll()
                                .requestMatchers(org.springframework.http.HttpMethod.GET, "/publicaciones", "/publicaciones/**").permitAll()
                                .requestMatchers(org.springframework.http.HttpMethod.GET, "/publicaciones/*/ofertas/usuario/*").permitAll()
                                .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/publicaciones/*/ofertas/usuario/*").authenticated()
                                .requestMatchers("/tarjetas/**").permitAll()
                                .anyRequest().authenticated()
                )
                .sessionManagement(sessionManager -> sessionManager.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // La configuración CORS se maneja en CorsConfig.java
}