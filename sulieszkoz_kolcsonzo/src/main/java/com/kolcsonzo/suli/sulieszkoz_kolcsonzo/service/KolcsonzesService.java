package com.kolcsonzo.suli.sulieszkoz_kolcsonzo.service;

import com.kolcsonzo.suli.sulieszkoz_kolcsonzo.dto.KolcsonzesDTO;
import com.kolcsonzo.suli.sulieszkoz_kolcsonzo.dto.KolcsonzesLetrehozoDTO;
import com.kolcsonzo.suli.sulieszkoz_kolcsonzo.enums.KolcsonzesStatuszEnum;
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

    public KolcsonzesService(KolcsonzesRepository kolcsonzesRepository,
                             FelhasznaloRepository felhasznaloRepository,
                             EszkozRepository eszkozRepository) {
        this.kolcsonzesRepository = kolcsonzesRepository;
        this.felhasznaloRepository = felhasznaloRepository;
        this.eszkozRepository = eszkozRepository;
    }

    // összes kölcsönzés lekérdezése
    public List<KolcsonzesDTO> getAllKolcsonzes() {
        return kolcsonzesRepository.findAll().stream()
                .map(this::convertToDTO)
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
        return convertToDTO(mentettKolcsonzes);
    }

    // segédmetódus
    private KolcsonzesDTO convertToDTO(Kolcsonzes k) {
        KolcsonzesDTO dto = new KolcsonzesDTO();
        dto.setId(k.getKolcsonzesId());

        dto.setFelhasznaloId(k.getFelhasznalo().getFelhasznaloId());
        dto.setFelhasznaloNev(k.getFelhasznalo().getNev());

        dto.setEszkozId(k.getEszkoz().getEszkozId());
        dto.setEszkozNev(k.getEszkoz().getNev());
        dto.setEszkozSku(k.getEszkoz().getSku());

        dto.setKiadoId(k.getKiado().getFelhasznaloId());
        dto.setKiadoNev(k.getKiado().getNev());

        dto.setStatuszNev(k.getStatusz().name());

        dto.setKiadasDatuma(k.getKiadasDatuma());
        dto.setVisszavetelDatuma(k.getVisszavetelDatuma());
        dto.setHatarido(k.getHatarido());

        return dto;
    }
}