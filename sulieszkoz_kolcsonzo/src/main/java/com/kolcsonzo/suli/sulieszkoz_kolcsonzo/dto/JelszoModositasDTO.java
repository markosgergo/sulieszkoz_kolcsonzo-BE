package com.kolcsonzo.suli.sulieszkoz_kolcsonzo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class JelszoModositasDTO {
    @NotBlank(message = "A régi jelszó megadása kötelező")
    private String regiJelszo;

    @NotBlank(message = "Az új jelszó megadása kötelező")
    private String ujJelszo;
}