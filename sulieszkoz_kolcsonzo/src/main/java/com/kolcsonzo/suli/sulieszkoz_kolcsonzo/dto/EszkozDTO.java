package com.kolcsonzo.suli.sulieszkoz_kolcsonzo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EszkozDTO {
    private Long id;
    private String nev;
    private String tipus;
    private String sku;
    private String leiras;
    private boolean elerheto;
}