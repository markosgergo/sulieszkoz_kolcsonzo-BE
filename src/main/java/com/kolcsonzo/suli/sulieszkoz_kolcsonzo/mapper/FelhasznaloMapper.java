package com.kolcsonzo.suli.sulieszkoz_kolcsonzo.mapper;

import com.kolcsonzo.suli.sulieszkoz_kolcsonzo.dto.FelhasznaloDTO;
import com.kolcsonzo.suli.sulieszkoz_kolcsonzo.model.Felhasznalo;
import org.springframework.stereotype.Component;

@Component
public class FelhasznaloMapper {

    public FelhasznaloDTO toDTO(Felhasznalo f) {
        if (f == null) return null;

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