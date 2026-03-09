package com.kolcsonzo.suli.sulieszkoz_kolcsonzo.service;

import com.kolcsonzo.suli.sulieszkoz_kolcsonzo.dto.FelhasznaloLetrehozoDTO;
import com.kolcsonzo.suli.sulieszkoz_kolcsonzo.dto.FelhasznaloDTO;
import com.kolcsonzo.suli.sulieszkoz_kolcsonzo.enums.FelhasznaloSzerepkor;
import com.kolcsonzo.suli.sulieszkoz_kolcsonzo.mapper.FelhasznaloMapper;
import com.kolcsonzo.suli.sulieszkoz_kolcsonzo.model.Felhasznalo;
import com.kolcsonzo.suli.sulieszkoz_kolcsonzo.model.Szerepkor;
import com.kolcsonzo.suli.sulieszkoz_kolcsonzo.repository.FelhasznaloRepository;
import com.kolcsonzo.suli.sulieszkoz_kolcsonzo.repository.SzerepkorRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FelhasznaloService {

    private final FelhasznaloRepository felhasznaloRepository;
    private final SzerepkorRepository szerepkorRepository;
    private final FelhasznaloMapper mapper;
    private final PasswordEncoder passwordEncoder;

    //mindkét Repository beinjektálása
    public FelhasznaloService(FelhasznaloRepository felhasznaloRepository, SzerepkorRepository szerepkorRepository, FelhasznaloMapper mapper, PasswordEncoder passwordEncoder) {
        this.felhasznaloRepository = felhasznaloRepository;
        this.szerepkorRepository = szerepkorRepository;
        this.mapper = mapper;
        this.passwordEncoder = passwordEncoder;
    }

    // osszes felhasználó lekérdezése (jelszó nelkul)
    public List<FelhasznaloDTO> getAllFelhasznalo() {
        return felhasznaloRepository.findAll().stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    // egy felhasználó lekérdezése ID alapján
    public FelhasznaloDTO getFelhasznaloById(Long id) {
        Felhasznalo f = felhasznaloRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Nem található felhasználó ezzel az ID-val: " + id));
        return mapper.toDTO(f);
    }

    // felhasználó lekérése email cím alapján (a /me végponthoz)
    public FelhasznaloDTO getFelhasznaloByEmail(String email) {
        Felhasznalo felhasznalo = felhasznaloRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Nem található felhasználó ezzel az email címmel: " + email));

        return mapper.toDTO(felhasznalo);
    }

    //új felhasználó létrehozása / Regisztráció
    public FelhasznaloDTO createFelhasznalo(FelhasznaloLetrehozoDTO dto) {
        Felhasznalo ujFelhasznalo = new Felhasznalo();
        ujFelhasznalo.setNev(dto.getNev());
        ujFelhasznalo.setEmail(dto.getEmail());

        ujFelhasznalo.setJelszo(passwordEncoder.encode(dto.getJelszo()));

        // String szerepkör -> enum, majd annak kikeresése az adatbázisból
        FelhasznaloSzerepkor enumSzerepkor = FelhasznaloSzerepkor.valueOf(dto.getSzerepkorNev().toUpperCase());
        Szerepkor szerepkor = szerepkorRepository.findBySzerepkorNev(enumSzerepkor)
                .orElseThrow(() -> new RuntimeException("Nem létező szerepkör: " + dto.getSzerepkorNev()));

        ujFelhasznalo.setSzerepkor(szerepkor);

        Felhasznalo mentettFelhasznalo = felhasznaloRepository.save(ujFelhasznalo);
        return mapper.toDTO(mentettFelhasznalo);
    }

    //felhasználó törlése
    public void deleteFelhasznalo(Long id) {
        if (!felhasznaloRepository.existsById(id)) {
            throw new RuntimeException("Nem található felhasználó ezzel az ID-val: " + id);
        }
        felhasznaloRepository.deleteById(id);
    }
}