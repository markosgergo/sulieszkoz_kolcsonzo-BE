package com.kolcsonzo.suli.sulieszkoz_kolcsonzo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SzerepkorModositasDTO {
    @NotBlank(message = "A szerepkör neve kötelező (pl. ADMIN, ALKALMAZOTT, FELHASZNALO)")
    private String ujSzerepkor;
}