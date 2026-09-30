package com.BookMyShow.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

     @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http){
         return http.
                 csrf(csrf-> csrf.disable())
                 .cors(Customizer.withDefaults())
                 .sessionManagement(s->s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                 .authorizeHttpRequests(auth -> auth
                         .requestMatchers(
                                 "/", "/index.html", "/login.html", "/register.html",
                                 "/css/**", "/js/**", "/images/**", "/favicon.ico"
                         ).permitAll()
                         .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                         .requestMatchers(HttpMethod.POST, "/api/v1/profiles").permitAll()
                         .requestMatchers("/api/v1/profiles/login").permitAll()
                         .requestMatchers(HttpMethod.GET, "/api/v1/movies/**",
                         "/api/v1/theatres/**", "/api/v1/shows/**").permitAll()
                         .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                         .requestMatchers("/api/v1/bookings/**","/api/v1/profiles/*/bookings")
                         .hasAnyRole("USER","ADMIN")
                         .anyRequest().authenticated())
                 .build();

     }
}
