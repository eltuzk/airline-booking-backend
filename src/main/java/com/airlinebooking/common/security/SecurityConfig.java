package com.airlinebooking.common.security;

import com.airlinebooking.auth.filter.AuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, AuthenticationFilter authenticantionFilter){


        return http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(request ->{
                    request.requestMatchers("/auth/**").permitAll();


                    request.anyRequest().authenticated();
                })
                .addFilterBefore(authenticantionFilter , UsernamePasswordAuthenticationFilter.class)
                .build();

    }
}
