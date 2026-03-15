package com.kolcsonzo.suli.sulieszkoz_kolcsonzo.service;

import com.kolcsonzo.suli.sulieszkoz_kolcsonzo.dto.KolcsonzesDTO;
import com.kolcsonzo.suli.sulieszkoz_kolcsonzo.dto.KolcsonzesLetrehozoDTO;
import com.kolcsonzo.suli.sulieszkoz_kolcsonzo.enums.KolcsonzesStatuszEnum;
import com.kolcsonzo.suli.sulieszkoz_kolcsonzo.exception.BusinessException;
import com.kolcsonzo.suli.sulieszkoz_kolcsonzo.exception.EntityNotFoundException;
import com.kolcsonzo.suli.sulieszkoz_kolcsonzo.mapper.KolcsonzesMapper;
import com.kolcsonzo.suli.sulieszkoz_kolcsonzo.model.Eszkoz;
import com.kolcsonzo.suli.sulieszkoz_kolcsonzo.model.Felhasznalo;
import com.kolcsonzo.suli.sulieszkoz_kolcsonzo.model.Kolcsonzes;
import com.kolcsonzo.suli.sulieszkoz_kolcsonzo.repository.EszkozRepository;
import com.kolcsonzo.suli.sulieszkoz_kolcsonzo.repository.FelhasznaloRepository;
import com.kolcsonzo.suli.sulieszkoz_kolcsonzo.repository.KolcsonzesRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
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
                             EszkozRepository eszkozRepository,
                             KolcsonzesMapper mapper) {
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
                .orElseThrow(() -> new EntityNotFoundException("Nem talalhato felhasznalo (diak) ezzel az ID-val: " + dto.getFelhasznaloId()));

        Felhasznalo kiado = felhasznaloRepository.findById(dto.getKiadoId())
                .orElseThrow(() -> new EntityNotFoundException("Nem talalhato kiado (tanar/admin) ezzel az ID-val: " + dto.getKiadoId()));

        Eszkoz eszkoz = eszkozRepository.findById(dto.getEszkozId())
                .orElseThrow(() -> new EntityNotFoundException("Nem talalhato eszkoz ezzel az ID-val: " + dto.getEszkozId()));

        // Uzleti szabaly: nem elerheto eszkoz nem kolcsonozhetelo -> BusinessException (400)
        if (!eszkoz.isElerheto()) {
            throw new BusinessException("Ez az eszkoz jelenleg nincs keszleten vagy ki van kolcsonozve!");
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

    //Késésben lévő kölcsönzések lekérése
    public List<KolcsonzesDTO> getKesesbenLevoKolcsonzesek() {
        LocalDate ma = LocalDate.now();
        return kolcsonzesRepository.findByStatuszAndHataridoBefore(KolcsonzesStatuszEnum.KIKOLCSONOZVE, ma).stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public KolcsonzesDTO visszaveszKolcsonzes(Long kolcsonzesId) {
        Kolcsonzes kolcsonzes = kolcsonzesRepository.findById(kolcsonzesId)
                .orElseThrow(() -> new EntityNotFoundException("Kolcsonzes nem talalhato ezzel az ID-val: " + kolcsonzesId));

        // Uzleti szabaly: mar visszaadott eszkoz nem veheteto vissza ujra -> BusinessException (400)
        if (kolcsonzes.getStatusz() == KolcsonzesStatuszEnum.VISSZAADVA) {
            throw new BusinessException("Ez az eszkoz mar vissza lett adva!");
        }

        kolcsonzes.setStatusz(KolcsonzesStatuszEnum.VISSZAADVA);
        kolcsonzes.setVisszavetelDatuma(LocalDateTime.now());

        Eszkoz eszkoz = kolcsonzes.getEszkoz();
        eszkoz.setElerheto(true);
        eszkozRepository.save(eszkoz);

        Kolcsonzes frissitett = kolcsonzesRepository.save(kolcsonzes);
        return mapper.toDTO(frissitett);
    }
}