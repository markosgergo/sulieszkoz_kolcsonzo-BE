package com.kolcsonzo.suli.sulieszkoz_kolcsonzo.mapper;

import com.kolcsonzo.suli.sulieszkoz_kolcsonzo.dto.EszkozDTO;
import com.kolcsonzo.suli.sulieszkoz_kolcsonzo.model.Eszkoz;
import org.springframework.stereotype.Component;

@Component
public class EszkozMapper {

    public EszkozDTO toDTO(Eszkoz eszkoz) {
        if (eszkoz == null) return null;

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