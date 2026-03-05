package com.kolcsonzo.suli.sulieszkoz_kolcsonzo.controller;

import com.kolcsonzo.suli.sulieszkoz_kolcsonzo.dto.LoginDTO;
import com.kolcsonzo.suli.sulieszkoz_kolcsonzo.security.CustomUserDetailsService;
import com.kolcsonzo.suli.sulieszkoz_kolcsonzo.security.JwtUtil;
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
        // megpróbáljuk hitelesíteni a felhasználót a megadott adatokkal
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDTO.getEmail(), loginDTO.getJelszo())
        );

        // kikeressuk az adatait
        final UserDetails userDetails = userDetailsService.loadUserByUsername(loginDTO.getEmail());

        //geenerálunk neki egy tokent
        final String jwt = jwtUtil.generateToken(userDetails);

        //visszaküldjük egy JSON-ben
        Map<String, String> response = new HashMap<>();
        response.put("token", jwt);

        return ResponseEntity.ok(response);
    }
}