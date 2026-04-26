package com.kolcsonzo.suli.sulieszkoz_kolcsonzo.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;

@Data
public class KolcsonzesLetrehozoDTO {

    @NotNull(message = "A diák azonosítója (felhasznaloId) kötelező!")
    private Long felhasznaloId;

    @NotNull(message = "Az eszköz azonosítója (eszkozId) kötelező!")
    private Long eszkozId;

    // Opcionális: diák foglaláskor nem adja meg, csak admin/alkalmazott általi közvetlen kiadásnál
    private Long kiadoId;

    @NotNull(message = "A határidő megadása kötelező!")
    @Future(message = "A határidő csak jövőbeli dátum lehet!") // Varázslat: nem enged múltbéli dátumot!
    private LocalDate hatarido;
}