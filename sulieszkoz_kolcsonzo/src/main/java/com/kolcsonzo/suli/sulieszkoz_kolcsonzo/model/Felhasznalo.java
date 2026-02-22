package com.kolcsonzo.suli.sulieszkoz_kolcsonzo.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Felhasznalo")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Felhasznalo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "felhasznalo_id")
    private Long felhasznaloId;

    @Column(name = "nev", length = 100)
    private String nev;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "jelszo", length = 255)
    private String jelszo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "szerepkor_id")
    private Szerepkor szerepkor;
}
