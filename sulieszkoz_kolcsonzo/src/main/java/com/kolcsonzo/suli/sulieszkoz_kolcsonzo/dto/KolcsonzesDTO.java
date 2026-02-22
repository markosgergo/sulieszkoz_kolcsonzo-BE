// sulieszkoz_kolcsonzo/src/main/java/com/kolcsonzo/suli/sulieszkoz_kolcsonzo/dto/KolcsonzesDTO.java
package com.kolcsonzo.suli.sulieszkoz_kolcsonzo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KolcsonzesDTO {

    private Long id;

    private Long felhasznaloId; //aki kolcsonozte
    private String felhasznaloNev;


    private Long eszkozId;
    private String eszkozNev;
    private String eszkozSku;

    private Long kiadoId;
    private String kiadoNev;

    private String statuszNev;

    private LocalDateTime kiadasDatuma;
    private LocalDateTime visszavetelDatuma;
    private LocalDate hatarido;
}