package com.kolcsonzo.suli.sulieszkoz_kolcsonzo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SzerepkorModositasDTO {
    @NotBlank(message = "A szerepkör neve kötelező (pl. ADMIN, ALKALMAZOTT, FELHASZNALO)")
    private String ujSzerepkor;
}