package com.kolcsonzo.suli.sulieszkoz_kolcsonzo.service;

import com.kolcsonzo.suli.sulieszkoz_kolcsonzo.dto.EszkozDTO;
import com.kolcsonzo.suli.sulieszkoz_kolcsonzo.model.Eszkoz;
import com.kolcsonzo.suli.sulieszkoz_kolcsonzo.repository.EszkozRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EszkozService {

    private final EszkozRepository repository;

    public EszkozService(EszkozRepository repository) {
        this.repository = repository;
    }

    //összes eszköz lekérdezése (READ)
    public List<EszkozDTO> getAllEszkoz() {
        return repository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    //egyetlen eszköz lekérdezése ID alapján (READ)
    public EszkozDTO getEszkozById(Long id) {
        Eszkoz eszkoz = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Nem található eszköz ezzel az ID-val: " + id));
        return convertToDTO(eszkoz);
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
        return convertToDTO(mentettEszkoz);
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
        return convertToDTO(frissitettEszkoz);
    }

    //eszköz törlése (DELETE)
    public void deleteEszkoz(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Nem található eszköz ezzel az ID-val: " + id);
        }
        repository.deleteById(id);
    }

    // metodus az atalakitashoz
    private EszkozDTO convertToDTO(Eszkoz eszkoz) {
        EszkozDTO dto = new EszkozDTO();
        dto.setId(eszkoz.getEszkozId());
        dto.setNev(eszkoz.getNev());
        dto.setTipus(eszkoz.getTipus());
        dto.setSku(eszkoz.getSku());
        dto.setLeiras(eszkoz.getLeiras());
        dto.setElerheto(eszkoz.isElerheto());
        return dto;
    }
}