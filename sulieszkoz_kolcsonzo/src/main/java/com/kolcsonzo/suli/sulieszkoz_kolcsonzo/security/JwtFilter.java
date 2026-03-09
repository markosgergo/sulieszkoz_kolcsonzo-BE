package com.kolcsonzo.suli.sulieszkoz_kolcsonzo.security;

import com.kolcsonzo.suli.sulieszkoz_kolcsonzo.service.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException; // ÚJ IMPORT
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService customUserDetailsService;

    public JwtFilter(JwtUtil jwtUtil, CustomUserDetailsService customUserDetailsService) {
        this.jwtUtil = jwtUtil;
        this.customUserDetailsService = customUserDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String email = null;
        String jwt = null;

        // 1. Kikeresjük a "jwt" nevű sütit a kérésből
        if (request.getCookies() != null) {
            for (jakarta.servlet.http.Cookie cookie : request.getCookies()) {
                if ("jwt".equals(cookie.getName())) {
                    jwt = cookie.getValue();
                    try {
                        email = jwtUtil.extractUsername(jwt);
                    } catch (Exception e) {
                        System.out.println("Érvénytelen vagy lejárt süti token!");
                    }
                    break;
                }
            }
        }

        // 2. Ha van email, de még nincs hitelesítve a kérés
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // ÚJ: Berakjuk egy try-catch blokkba, ha az adatbázisban már nem létezik a felhasználó!
            try {
                UserDetails userDetails = this.customUserDetailsService.loadUserByUsername(email);

                if (jwtUtil.validateToken(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            } catch (Exception e) { // <-- EZT ÍRTUK ÁT Exception-re!
                System.out.println("Hiba a token ellenőrzésekor, a süti figyelmen kívül hagyva: " + e.getMessage());
            }
        }

        filterChain.doFilter(request, response);
    }
}