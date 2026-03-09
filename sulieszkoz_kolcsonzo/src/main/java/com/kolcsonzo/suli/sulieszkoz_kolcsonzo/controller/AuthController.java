package com.kolcsonzo.suli.sulieszkoz_kolcsonzo.controller;

import com.kolcsonzo.suli.sulieszkoz_kolcsonzo.dto.LoginDTO;
import com.kolcsonzo.suli.sulieszkoz_kolcsonzo.service.CustomUserDetailsService;
import com.kolcsonzo.suli.sulieszkoz_kolcsonzo.security.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;

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
        // megpróbáljuk hitelesíteni a felhasználót a megadott adatokkal
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDTO.getEmail(), loginDTO.getJelszo())
        );

        // kikeressuk az adatait
        final UserDetails userDetails = userDetailsService.loadUserByUsername(loginDTO.getEmail());

        //geenerálunk neki egy tokent
        final String jwt = jwtUtil.generateToken(userDetails);

        ResponseCookie cookie = ResponseCookie.from("jwt", jwt)
                .httpOnly(true)    // JavaScript nem fér hozzá (XSS védelem)
                .secure(false)     // Fejlesztés alatt (localhost) false, élesben (HTTPS) true kell legyen!
                .path("/")         // A teljes weblapra érvényes
                .maxAge(10 * 60 * 60) // 10 óráig érvényes (másodpercben)
                .sameSite("Strict") // CSRF támadások elleni védelem
                .build();

        //visszaküldjük egy JSON-ben
        Map<String, String> response = new HashMap<>();
        response.put("token", jwt);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(response);    }
}