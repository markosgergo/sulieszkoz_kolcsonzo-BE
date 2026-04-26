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

    // kiadásra váró kérelmek lekérése (admin/alkalmazott számára)
    public List<KolcsonzesDTO> getKiadasraVarokList() {
        return kolcsonzesRepository.findByStatusz(KolcsonzesStatuszEnum.KIADASRA_VAR).stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    // új foglalási kérelem indítása diák által -> KIADASRA_VAR státusz
    @Transactional
    public KolcsonzesDTO createKolcsonzes(KolcsonzesLetrehozoDTO dto) {
        Felhasznalo diak = felhasznaloRepository.findById(dto.getFelhasznaloId())
                .orElseThrow(() -> new EntityNotFoundException("Nem talalhato felhasznalo (diak) ezzel az ID-val: " + dto.getFelhasznaloId()));

        Eszkoz eszkoz = eszkozRepository.findById(dto.getEszkozId())
                .orElseThrow(() -> new EntityNotFoundException("Nem talalhato eszkoz ezzel az ID-val: " + dto.getEszkozId()));

        // Üzleti szabály: nem elérhető eszköz nem foglalható
        if (!eszkoz.isElerheto()) {
            throw new BusinessException("Ez az eszkoz jelenleg nincs keszleten vagy ki van kolcsonozve!");
        }

        // Ha van kiadoId (pl. admin/alkalmazott közvetlen kiadása), azonnal KIKOLCSONOZVE
        if (dto.getKiadoId() != null) {
            Felhasznalo kiado = felhasznaloRepository.findById(dto.getKiadoId())
                    .orElseThrow(() -> new EntityNotFoundException("Nem talalhato kiado ezzel az ID-val: " + dto.getKiadoId()));

            eszkoz.setElerheto(false);
            eszkozRepository.save(eszkoz);

            Kolcsonzes ujKolcsonzes = new Kolcsonzes();
            ujKolcsonzes.setFelhasznalo(diak);
            ujKolcsonzes.setEszkoz(eszkoz);
            ujKolcsonzes.setKiado(kiado);
            ujKolcsonzes.setKiadasDatuma(LocalDateTime.now());
            ujKolcsonzes.setHatarido(dto.getHatarido());
            ujKolcsonzes.setStatusz(KolcsonzesStatuszEnum.KIKOLCSONOZVE);

            return mapper.toDTO(kolcsonzesRepository.save(ujKolcsonzes));
        }

        // Diák foglalása: KIADASRA_VAR, az eszközt lezárjuk hogy mások ne foglalhassák
        eszkoz.setElerheto(false);
        eszkozRepository.save(eszkoz);
        Kolcsonzes ujKolcsonzes = new Kolcsonzes();
        ujKolcsonzes.setFelhasznalo(diak);
        ujKolcsonzes.setEszkoz(eszkoz);
        ujKolcsonzes.setKiado(null); // még nincs jóváhagyó
        ujKolcsonzes.setKiadasDatuma(null); // még nem adták ki ténylegesen
        ujKolcsonzes.setHatarido(dto.getHatarido());
        ujKolcsonzes.setStatusz(KolcsonzesStatuszEnum.KIADASRA_VAR);

        return mapper.toDTO(kolcsonzesRepository.save(ujKolcsonzes));
    }

    // Alkalmazott/admin jóváhagyja a kiadást – email alapján (a bejelentkezett user lesz a kiadó)
    @Transactional
    public KolcsonzesDTO elfogadKiadasKerelemetEmail(Long kolcsonzesId, String kiadoEmail) {
        Kolcsonzes kolcsonzes = kolcsonzesRepository.findById(kolcsonzesId)
                .orElseThrow(() -> new EntityNotFoundException("Kolcsonzes nem talalhato ezzel az ID-val: " + kolcsonzesId));

        if (kolcsonzes.getStatusz() != KolcsonzesStatuszEnum.KIADASRA_VAR) {
            throw new BusinessException("Ez a kérelem nem 'kiadásra vár' státuszban van, nem hagyható jóvá!");
        }

        Felhasznalo kiado = felhasznaloRepository.findByEmail(kiadoEmail)
                .orElseThrow(() -> new EntityNotFoundException("Nem talalhato kiado ezzel az emaillel: " + kiadoEmail));

        // Az eszköz már false-ra volt állítva a foglalásnál, csak státuszt váltunk
        kolcsonzes.setKiado(kiado);
        kolcsonzes.setKiadasDatuma(LocalDateTime.now());
        kolcsonzes.setStatusz(KolcsonzesStatuszEnum.KIKOLCSONOZVE);

        return mapper.toDTO(kolcsonzesRepository.save(kolcsonzes));
    }

    // Alkalmazott/admin jóváhagyja a kiadást -> KIKOLCSONOZVE
    @Transactional
    public KolcsonzesDTO elfogadKiadasKerelem(Long kolcsonzesId, Long kiadoId) {
        Kolcsonzes kolcsonzes = kolcsonzesRepository.findById(kolcsonzesId)
                .orElseThrow(() -> new EntityNotFoundException("Kolcsonzes nem talalhato ezzel az ID-val: " + kolcsonzesId));

        if (kolcsonzes.getStatusz() != KolcsonzesStatuszEnum.KIADASRA_VAR) {
            throw new BusinessException("Ez a kérelem nem 'kiadásra vár' státuszban van, nem hagyható jóvá!");
        }

        Felhasznalo kiado = felhasznaloRepository.findById(kiadoId)
                .orElseThrow(() -> new EntityNotFoundException("Nem talalhato kiado ezzel az ID-val: " + kiadoId));

        Eszkoz eszkoz = kolcsonzes.getEszkoz();
        if (!eszkoz.isElerheto()) {
            throw new BusinessException("Ez az eszkoz időközben már kiadásra kerülhetett!");
        }

        eszkoz.setElerheto(false);
        eszkozRepository.save(eszkoz);

        kolcsonzes.setKiado(kiado);
        kolcsonzes.setKiadasDatuma(LocalDateTime.now());
        kolcsonzes.setStatusz(KolcsonzesStatuszEnum.KIKOLCSONOZVE);

        return mapper.toDTO(kolcsonzesRepository.save(kolcsonzes));
    }

    // Alkalmazott/admin elutasítja a kiadási kérelmet -> törli
    @Transactional
    public void elutasitKiadasKerelem(Long kolcsonzesId) {
        Kolcsonzes kolcsonzes = kolcsonzesRepository.findById(kolcsonzesId)
                .orElseThrow(() -> new EntityNotFoundException("Kolcsonzes nem talalhato ezzel az ID-val: " + kolcsonzesId));

        if (kolcsonzes.getStatusz() != KolcsonzesStatuszEnum.KIADASRA_VAR) {
            throw new BusinessException("Csak 'kiadásra vár' státuszú kérelem utasítható el!");
        }

        // Eszközt visszaállítjuk elérhetőre
        Eszkoz eszkoz = kolcsonzes.getEszkoz();
        eszkoz.setElerheto(true);
        eszkozRepository.save(eszkoz);

        kolcsonzesRepository.delete(kolcsonzes);
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
    @Transactional
    public KolcsonzesDTO visszaveszByEszkozId(Long eszkozId) {
        // Megkeressük az adott eszközhöz tartozó AKTÍV (kikölcsönzött) státuszú kölcsönzést
        Kolcsonzes kolcsonzes = kolcsonzesRepository.findByEszkoz_EszkozIdAndStatusz(eszkozId, KolcsonzesStatuszEnum.KIKOLCSONOZVE)
                .orElseThrow(() -> new BusinessException("Nincs aktív kölcsönzés ehhez az eszközhöz, vagy már vissza lett adva!"));

        kolcsonzes.setStatusz(KolcsonzesStatuszEnum.VISSZAADVA);
        kolcsonzes.setVisszavetelDatuma(LocalDateTime.now());

        Eszkoz eszkoz = kolcsonzes.getEszkoz();
        eszkoz.setElerheto(true);
        eszkozRepository.save(eszkoz);

        Kolcsonzes frissitett = kolcsonzesRepository.save(kolcsonzes);
        return mapper.toDTO(frissitett);
    }
}