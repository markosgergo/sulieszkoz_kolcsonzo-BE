package com.kolcsonzo.suli.sulieszkoz_kolcsonzo.service;

import com.kolcsonzo.suli.sulieszkoz_kolcsonzo.dto.KolcsonzesDTO;
import com.kolcsonzo.suli.sulieszkoz_kolcsonzo.dto.KolcsonzesLetrehozoDTO;
import com.kolcsonzo.suli.sulieszkoz_kolcsonzo.enums.KolcsonzesStatuszEnum;
import com.kolcsonzo.suli.sulieszkoz_kolcsonzo.mapper.KolcsonzesMapper;
import com.kolcsonzo.suli.sulieszkoz_kolcsonzo.model.Eszkoz;
import com.kolcsonzo.suli.sulieszkoz_kolcsonzo.model.Felhasznalo;
import com.kolcsonzo.suli.sulieszkoz_kolcsonzo.model.Kolcsonzes;
import com.kolcsonzo.suli.sulieszkoz_kolcsonzo.repository.EszkozRepository;
import com.kolcsonzo.suli.sulieszkoz_kolcsonzo.repository.FelhasznaloRepository;
import com.kolcsonzo.suli.sulieszkoz_kolcsonzo.repository.KolcsonzesRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class KolcsonzesService {

    private final KolcsonzesRepository kolcsonzesRepository;
    private final FelhasznaloRepository felhasznaloRepository;
    private final EszkozRepository eszkozRepository;
    private final KolcsonzesMapper mapper;

    public KolcsonzesService(KolcsonzesRepository kolcsonzesRepository,
                             FelhasznaloRepository felhasznaloRepository,
                             EszkozRepository eszkozRepository, KolcsonzesMapper mapper) {
        this.kolcsonzesRepository = kolcsonzesRepository;
        this.felhasznaloRepository = felhasznaloRepository;
        this.eszkozRepository = eszkozRepository;
        this.mapper = mapper;
    }

    // összes kölcsönzés lekérdezése
    public List<KolcsonzesDTO> getAllKolcsonzes() {
        return kolcsonzesRepository.findAll().stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    // saját kölcsönzések lekérése email alapján
    public List<KolcsonzesDTO> getSajatKolcsonzesek(String email) {
        return kolcsonzesRepository.findByFelhasznalo_Email(email).stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    // új kölcsönzés indítása
    @Transactional
    public KolcsonzesDTO createKolcsonzes(KolcsonzesLetrehozoDTO dto) {
        Felhasznalo diak = felhasznaloRepository.findById(dto.getFelhasznaloId())
                .orElseThrow(() -> new RuntimeException("Nem található felhasználó (diák)."));

        Felhasznalo kiado = felhasznaloRepository.findById(dto.getKiadoId())
                .orElseThrow(() -> new RuntimeException("Nem található kiadó (tanár/admin)."));

        Eszkoz eszkoz = eszkozRepository.findById(dto.getEszkozId())
                .orElseThrow(() -> new RuntimeException("Nem található eszköz."));

        if (!eszkoz.isElerheto()) {
            throw new RuntimeException("Ez az eszköz jelenleg nincs készleten vagy ki van kölcsönözve!");
        }

        eszkoz.setElerheto(false);
        eszkozRepository.save(eszkoz);

        Kolcsonzes ujKolcsonzes = new Kolcsonzes();
        ujKolcsonzes.setFelhasznalo(diak);
        ujKolcsonzes.setEszkoz(eszkoz);
        ujKolcsonzes.setKiado(kiado);
        ujKolcsonzes.setKiadasDatuma(LocalDateTime.now());
        ujKolcsonzes.setHatarido(dto.getHatarido());
        ujKolcsonzes.setStatusz(KolcsonzesStatuszEnum.KIKOLCSONOZVE);

        Kolcsonzes mentettKolcsonzes = kolcsonzesRepository.save(ujKolcsonzes);
        return mapper.toDTO(mentettKolcsonzes);
    }
}