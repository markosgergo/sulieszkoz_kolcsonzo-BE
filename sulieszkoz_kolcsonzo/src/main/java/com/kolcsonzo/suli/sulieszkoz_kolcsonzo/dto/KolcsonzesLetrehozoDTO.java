package com.kolcsonzo.suli.sulieszkoz_kolcsonzo.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class KolcsonzesLetrehozoDTO {
    private Long felhasznaloId;
    private Long eszkozId;
    private Long kiadoId;
    private LocalDate hatarido;
}