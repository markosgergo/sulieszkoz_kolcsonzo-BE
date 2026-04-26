package com.kolcsonzo.suli.sulieszkoz_kolcsonzo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JelszoModositasDTO {
    @NotBlank(message = "A régi jelszó megadása kötelező")
    private String regiJelszo;

    @NotBlank(message = "Az új jelszó megadása kötelező")
    private String ujJelszo;
}