package com.kolcsonzo.suli.sulieszkoz_kolcsonzo.model;

import com.kolcsonzo.suli.sulieszkoz_kolcsonzo.enums.KolcsonzesStatuszEnum;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "Kolcsonzes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Kolcsonzes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "kolcsonzes_id")
    private Long kolcsonzesId;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "felhasznalo_id")
    private Felhasznalo felhasznalo;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "eszkoz_id")
    private Eszkoz eszkoz;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kiado_id")
    private Felhasznalo kiado;


    @Enumerated(EnumType.STRING)
    @Column(name = "statusz")
    private KolcsonzesStatuszEnum statusz;

    @Column(name = "kiadas_datuma")
    private LocalDateTime kiadasDatuma;

    @Column(name = "visszavetel_datuma")
    private LocalDateTime visszavetelDatuma;

    @Column(name = "hatarido")
    private LocalDate hatarido;
}

