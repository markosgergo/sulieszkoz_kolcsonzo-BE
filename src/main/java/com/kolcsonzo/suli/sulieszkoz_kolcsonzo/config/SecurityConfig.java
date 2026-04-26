package com.kolcsonzo.suli.sulieszkoz_kolcsonzo.config;

import com.kolcsonzo.suli.sulieszkoz_kolcsonzo.security.JwtFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(org.springframework.security.config.Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable) // REST apinál kikapcsoljuk a CSRF-et
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // nem kell használni, van jwt
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/login").permitAll() // bejelentkezés mindenkinek nyilvános
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/felhasznalok").permitAll() //regisztráció mindenkinek nyilvános
                        .requestMatchers("/error").permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/kolcsonzesek").hasAnyRole("ADMIN", "ALKALMAZOTT")
                        .requestMatchers("/api/kolcsonzesek/kesesben").hasAnyRole("ADMIN","ALKALMAZOTT") // Csak ADMIN vagy TANÁR férhet hozzá
                        .requestMatchers("/api/kolcsonzesek/{id}/visszavetel").hasAnyRole("ADMIN","ALKALMAZOTT")
                        .anyRequest().authenticated() // minden mashoz kotelezo bejelentkezni (kell a token)
                )

                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

}