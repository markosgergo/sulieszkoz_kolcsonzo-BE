package com.kolcsonzo.suli.sulieszkoz_kolcsonzo.service;

import com.kolcsonzo.suli.sulieszkoz_kolcsonzo.dto.FelhasznaloDTO;
import com.kolcsonzo.suli.sulieszkoz_kolcsonzo.dto.FelhasznaloLetrehozoDTO;
import com.kolcsonzo.suli.sulieszkoz_kolcsonzo.enums.FelhasznaloSzerepkor;
import com.kolcsonzo.suli.sulieszkoz_kolcsonzo.exception.EntityNotFoundException;
import com.kolcsonzo.suli.sulieszkoz_kolcsonzo.mapper.FelhasznaloMapper;
import com.kolcsonzo.suli.sulieszkoz_kolcsonzo.model.Felhasznalo;
import com.kolcsonzo.suli.sulieszkoz_kolcsonzo.model.Szerepkor;
import com.kolcsonzo.suli.sulieszkoz_kolcsonzo.repository.FelhasznaloRepository;
import com.kolcsonzo.suli.sulieszkoz_kolcsonzo.repository.SzerepkorRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FelhasznaloService {

    private final FelhasznaloRepository felhasznaloRepository;
    private final SzerepkorRepository szerepkorRepository;
    private final FelhasznaloMapper mapper;
    private final PasswordEncoder passwordEncoder;

    public FelhasznaloService(FelhasznaloRepository felhasznaloRepository,
                              SzerepkorRepository szerepkorRepository,
                              FelhasznaloMapper mapper,
                              PasswordEncoder passwordEncoder) {
        this.felhasznaloRepository = felhasznaloRepository;
        this.szerepkorRepository = szerepkorRepository;
        this.mapper = mapper;
        this.passwordEncoder = passwordEncoder;
    }

    // osszes felhasználó lekérdezése (jelszó nelkul)
    public List<FelhasznaloDTO> getAllFelhasznalo() {
        return felhasznaloRepository.findByToroltFalse().stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    // egy felhasználó lekérdezése ID alapján
    public FelhasznaloDTO getFelhasznaloById(Long id) {
        Felhasznalo f = felhasznaloRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Nem talalhato felhasznalo ezzel az ID-val: " + id));
        return mapper.toDTO(f);
    }

    // felhasználó lekérése email cím alapján (a /me végponthoz)
    public FelhasznaloDTO getFelhasznaloByEmail(String email) {
        Felhasznalo felhasznalo = felhasznaloRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Nem talalhato felhasznalo ezzel az email cimmel: " + email));
        return mapper.toDTO(felhasznalo);
    }

    //új felhasználó létrehozása / Regisztráció
    public FelhasznaloDTO createFelhasznalo(FelhasznaloLetrehozoDTO dto) {
        Felhasznalo ujFelhasznalo = new Felhasznalo();
        ujFelhasznalo.setNev(dto.getNev());
        ujFelhasznalo.setEmail(dto.getEmail());
        ujFelhasznalo.setJelszo(passwordEncoder.encode(dto.getJelszo()));

        FelhasznaloSzerepkor enumSzerepkor = FelhasznaloSzerepkor.valueOf(dto.getSzerepkorNev().toUpperCase());
        Szerepkor szerepkor = szerepkorRepository.findBySzerepkorNev(enumSzerepkor)
                .orElseThrow(() -> new EntityNotFoundException("Nem letező szerepkor: " + dto.getSzerepkorNev()));

        ujFelhasznalo.setSzerepkor(szerepkor);

        Felhasznalo mentettFelhasznalo = felhasznaloRepository.save(ujFelhasznalo);
        return mapper.toDTO(mentettFelhasznalo);
    }

    //felhasználó törlése
    public void deleteFelhasznalo(Long id) {
        Felhasznalo f = felhasznaloRepository.findById(id)
                .orElseThrow(()-> new EntityNotFoundException("Nem talalhato felhasznalo ezzel az ID-val: " + id));
        f.setTorolt(true);
        felhasznaloRepository.save(f);
    }

    public List<FelhasznaloDTO> keresesNevAlapjan(String nev) {
        return felhasznaloRepository.findByNevContainingIgnoreCase(nev).stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    // JELSZÓ MÓDOSÍTÁSA
    @Transactional
    public void modositJelszo(Long id, com.kolcsonzo.suli.sulieszkoz_kolcsonzo.dto.JelszoModositasDTO dto) {
        Felhasznalo felhasznalo = felhasznaloRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Felhasználó nem található: " + id));

        // Ha rossz a régi jelszó, BusinessException-t dobunk (ez nem okoz 500-as szerverhibát!)
        if (!passwordEncoder.matches(dto.getRegiJelszo(), felhasznalo.getJelszo())) {
            throw new com.kolcsonzo.suli.sulieszkoz_kolcsonzo.exception.BusinessException("A megadott régi jelszó helytelen!");
        }

        // Új jelszó kódolása és mentése
        felhasznalo.setJelszo(passwordEncoder.encode(dto.getUjJelszo()));
        felhasznaloRepository.save(felhasznalo);
    }

    // SZEREPKÖR MÓDOSÍTÁSA
    @Transactional
    public FelhasznaloDTO modositSzerepkor(Long id, String ujSzerepkorNev) {
        Felhasznalo felhasznalo = felhasznaloRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Felhasználó nem található: " + id));

        FelhasznaloSzerepkor enumSzerepkor;
        try {
            enumSzerepkor = FelhasznaloSzerepkor.valueOf(ujSzerepkorNev.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new com.kolcsonzo.suli.sulieszkoz_kolcsonzo.exception.BusinessException("Érvénytelen szerepkör: " + ujSzerepkorNev);
        }

        Szerepkor szerepkor = szerepkorRepository.findBySzerepkorNev(enumSzerepkor)
                .orElseThrow(() -> new EntityNotFoundException("Nem létező szerepkör az adatbázisban: " + ujSzerepkorNev));

        felhasznalo.setSzerepkor(szerepkor);
        Felhasznalo frissitett = felhasznaloRepository.save(felhasznalo);
        return mapper.toDTO(frissitett);
    }
}