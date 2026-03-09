package com.kolcsonzo.suli.sulieszkoz_kolcsonzo.service;

import com.kolcsonzo.suli.sulieszkoz_kolcsonzo.dto.EszkozDTO;
import com.kolcsonzo.suli.sulieszkoz_kolcsonzo.model.Eszkoz;
import com.kolcsonzo.suli.sulieszkoz_kolcsonzo.repository.EszkozRepository;
import org.springframework.stereotype.Service;
import com.kolcsonzo.suli.sulieszkoz_kolcsonzo.mapper.EszkozMapper;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EszkozService {

    private final EszkozRepository repository;
    private final EszkozMapper mapper;

    public EszkozService(EszkozRepository repository, EszkozMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    //összes eszköz lekérdezése (READ)
    public List<EszkozDTO> getAllEszkoz() {
        return repository.findAll().stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    // csak a szabad eszközök lekérése
    public List<EszkozDTO> getSzabadEszkozok() {
        return repository.findByElerhetoTrue().stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    //egyetlen eszköz lekérdezése ID alapján (READ)
    public EszkozDTO getEszkozById(Long id) {
        Eszkoz eszkoz = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Nem található eszköz ezzel az ID-val: " + id));
        return mapper.toDTO(eszkoz);
    }

    //új eszköz létrehozása (CREATE)
    public EszkozDTO createEszkoz(EszkozDTO dto) {
        Eszkoz ujEszkoz = new Eszkoz();
        ujEszkoz.setNev(dto.getNev());
        ujEszkoz.setTipus(dto.getTipus());
        ujEszkoz.setSku(dto.getSku());
        ujEszkoz.setLeiras(dto.getLeiras());
        ujEszkoz.setElerheto(dto.isElerheto());

        //mentés
        Eszkoz mentettEszkoz = repository.save(ujEszkoz);
        return mapper.toDTO(mentettEszkoz);
    }

    // meglévő eszköz módosítása (UPDATE)
    public EszkozDTO updateEszkoz(Long id, EszkozDTO dto) {
        Eszkoz meglevoEszkoz = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Nem található eszköz ezzel az ID-val: " + id));

        meglevoEszkoz.setNev(dto.getNev());
        meglevoEszkoz.setTipus(dto.getTipus());
        meglevoEszkoz.setSku(dto.getSku());
        meglevoEszkoz.setLeiras(dto.getLeiras());
        meglevoEszkoz.setElerheto(dto.isElerheto());

        Eszkoz frissitettEszkoz = repository.save(meglevoEszkoz);
        return mapper.toDTO(frissitettEszkoz);
    }

    //eszköz törlése (DELETE)
    public void deleteEszkoz(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Nem található eszköz ezzel az ID-val: " + id);
        }
        repository.deleteById(id);
    }
}