package com.kolcsonzo.suli.sulieszkoz_kolcsonzo.controller;

import com.kolcsonzo.suli.sulieszkoz_kolcsonzo.dto.LoginDTO;
import com.kolcsonzo.suli.sulieszkoz_kolcsonzo.service.CustomUserDetailsService;
import com.kolcsonzo.suli.sulieszkoz_kolcsonzo.security.JwtUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus; // ÚJ IMPORT
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;

    public AuthController(AuthenticationManager authenticationManager, CustomUserDetailsService userDetailsService, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginDTO loginDTO) {

        // 1. Megpróbáljuk hitelesíteni a felhasználót a megadott adatokkal
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDTO.getEmail(), loginDTO.getJelszo())
            );
        } catch (Exception e) {
            // HA HIBÁS A JELSZÓ VAGY AZ EMAIL CÍM:
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("hiba", "Hibás email cím vagy jelszó!");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }

        // 2. Ha sikeres, kikeressük az adatait
        final UserDetails userDetails = userDetailsService.loadUserByUsername(loginDTO.getEmail());

        // 3. Generálunk neki egy tokent
        final String jwt = jwtUtil.generateToken(userDetails);

        // 4. Létrehozzuk az HttpOnly Sütit
        ResponseCookie cookie = ResponseCookie.from("jwt", jwt)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(10 * 60 * 60)
                .sameSite("Strict")
                .build();

        // 5. Összeállítjuk a szép JSON üzenetet
        Map<String, String> response = new HashMap<>();
        response.put("uzenet", "Sikeres bejelentkezés!");

        // 6. A Sütit a fejlécben küldjük vissza
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(response);
    }
}