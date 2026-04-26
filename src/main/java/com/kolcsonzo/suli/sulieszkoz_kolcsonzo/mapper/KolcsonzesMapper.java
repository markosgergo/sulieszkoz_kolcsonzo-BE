package com.kolcsonzo.suli.sulieszkoz_kolcsonzo.mapper;

import com.kolcsonzo.suli.sulieszkoz_kolcsonzo.dto.KolcsonzesDTO;
import com.kolcsonzo.suli.sulieszkoz_kolcsonzo.model.Kolcsonzes;
import org.springframework.stereotype.Component;

@Component
public class KolcsonzesMapper {

    public KolcsonzesDTO toDTO(Kolcsonzes k) {
        if (k == null) return null;

        KolcsonzesDTO dto = new KolcsonzesDTO();
        dto.setId(k.getKolcsonzesId());

        dto.setFelhasznaloId(k.getFelhasznalo().getFelhasznaloId());
        dto.setFelhasznaloNev(k.getFelhasznalo().getNev());

        dto.setEszkozId(k.getEszkoz().getEszkozId());
        dto.setEszkozNev(k.getEszkoz().getNev());
        dto.setEszkozSku(k.getEszkoz().getSku());

        if (k.getKiado() != null) {
            dto.setKiadoId(k.getKiado().getFelhasznaloId());
            dto.setKiadoNev(k.getKiado().getNev());
        }

        dto.setStatuszNev(k.getStatusz().name());

        dto.setKiadasDatuma(k.getKiadasDatuma());
        dto.setVisszavetelDatuma(k.getVisszavetelDatuma());
        dto.setHatarido(k.getHatarido());

        return dto;
    }
}