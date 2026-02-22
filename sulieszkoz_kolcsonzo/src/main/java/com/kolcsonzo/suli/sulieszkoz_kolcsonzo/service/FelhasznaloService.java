package com.kolcsonzo.suli.sulieszkoz_kolcsonzo.service;

import com.kolcsonzo.suli.sulieszkoz_kolcsonzo.dto.FelhasznaloLetrehozoDTO;
import com.kolcsonzo.suli.sulieszkoz_kolcsonzo.dto.FelhasznaloDTO;
import com.kolcsonzo.suli.sulieszkoz_kolcsonzo.enums.FelhasznaloSzerepkor;
import com.kolcsonzo.suli.sulieszkoz_kolcsonzo.model.Felhasznalo;
import com.kolcsonzo.suli.sulieszkoz_kolcsonzo.model.Szerepkor;
import com.kolcsonzo.suli.sulieszkoz_kolcsonzo.repository.FelhasznaloRepository;
import com.kolcsonzo.suli.sulieszkoz_kolcsonzo.repository.SzerepkorRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FelhasznaloService {

    private final FelhasznaloRepository felhasznaloRepository;
    private final SzerepkorRepository szerepkorRepository;

    //mindkét Repository beinjektálása
    public FelhasznaloService(FelhasznaloRepository felhasznaloRepository, SzerepkorRepository szerepkorRepository) {
        this.felhasznaloRepository = felhasznaloRepository;
        this.szerepkorRepository = szerepkorRepository;
    }

    // osszes felhasználó lekérdezése (jelszó nelkul)
    public List<FelhasznaloDTO> getAllFelhasznalo() {
        return felhasznaloRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // egy felhasználó lekérdezése ID alapján
    public FelhasznaloDTO getFelhasznaloById(Long id) {
        Felhasznalo f = felhasznaloRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Nem található felhasználó ezzel az ID-val: " + id));
        return convertToDTO(f);
    }

    //új felhasználó létrehozása / Regisztráció
    public FelhasznaloDTO createFelhasznalo(FelhasznaloLetrehozoDTO dto) {
        Felhasznalo ujFelhasznalo = new Felhasznalo();
        ujFelhasznalo.setNev(dto.getNev());
        ujFelhasznalo.setEmail(dto.getEmail());

        //ITT KÉSŐBB TITKOSÍTANI KELL A JELSZÓT pl BCrypt
        // Ideiglenesen szovegkent taroljuk
        ujFelhasznalo.setJelszo(dto.getJelszo());

        // String szerepkör -> enum, majd annak kikeresése az adatbázisból
        FelhasznaloSzerepkor enumSzerepkor = FelhasznaloSzerepkor.valueOf(dto.getSzerepkorNev().toUpperCase());
        Szerepkor szerepkor = szerepkorRepository.findBySzerepkorNev(enumSzerepkor)
                .orElseThrow(() -> new RuntimeException("Nem létező szerepkör: " + dto.getSzerepkorNev()));

        ujFelhasznalo.setSzerepkor(szerepkor);

        Felhasznalo mentettFelhasznalo = felhasznaloRepository.save(ujFelhasznalo);
        return convertToDTO(mentettFelhasznalo);
    }

    //felhasználó törlése
    public void deleteFelhasznalo(Long id) {
        if (!felhasznaloRepository.existsById(id)) {
            throw new RuntimeException("Nem található felhasználó ezzel az ID-val: " + id);
        }
        felhasznaloRepository.deleteById(id);
    }

    // átalakításhoz segéd metódus
    private FelhasznaloDTO convertToDTO(Felhasznalo f) {
        FelhasznaloDTO dto = new FelhasznaloDTO();
        dto.setId(f.getFelhasznaloId());
        dto.setNev(f.getNev());
        dto.setEmail(f.getEmail());
        if (f.getSzerepkor() != null) {
            dto.setSzerepkorNev(f.getSzerepkor().getSzerepkorNev().name());
        }
        return dto;
    }
}