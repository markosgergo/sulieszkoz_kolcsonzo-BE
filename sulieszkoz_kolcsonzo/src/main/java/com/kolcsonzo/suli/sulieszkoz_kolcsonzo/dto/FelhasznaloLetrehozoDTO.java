package com.kolcsonzo.suli.sulieszkoz_kolcsonzo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class FelhasznaloLetrehozoDTO {

    @NotBlank(message = "A név megadása kötelező!")
    private String nev;

    @NotBlank(message = "Az email megadása kötelező!")
    @Email(message = "Érvénytelen email formátum!")
    private String email;

    @NotBlank(message = "A jelszó nem lehet üres!")
    @Size(min = 6, message = "A jelszónak legalább 6 karakternek kell lennie!")
    private String jelszo;

    @NotBlank(message = "A szerepkör megadása kötelező!")
    private String szerepkorNev;
}