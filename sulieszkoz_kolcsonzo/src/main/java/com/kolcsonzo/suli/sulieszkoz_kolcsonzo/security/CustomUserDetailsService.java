package com.kolcsonzo.suli.sulieszkoz_kolcsonzo.security;

import com.kolcsonzo.suli.sulieszkoz_kolcsonzo.model.Felhasznalo;
import com.kolcsonzo.suli.sulieszkoz_kolcsonzo.repository.FelhasznaloRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final FelhasznaloRepository felhasznaloRepository;

    public CustomUserDetailsService(FelhasznaloRepository felhasznaloRepository) {
        this.felhasznaloRepository = felhasznaloRepository;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Felhasznalo felhasznalo = felhasznaloRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Nem található felhasználó ezzel az emaillel: " + email));
        return User.builder()
                .username(felhasznalo.getEmail())
                .password(felhasznalo.getJelszo())
                .roles(felhasznalo.getSzerepkor().getSzerepkorNev().name())
                .build();
    }
}