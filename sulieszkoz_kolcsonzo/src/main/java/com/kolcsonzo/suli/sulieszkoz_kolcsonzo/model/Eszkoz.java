package com.kolcsonzo.suli.sulieszkoz_kolcsonzo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "eszkoz")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Eszkoz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "eszkoz_id")
    private Integer eszkozId;

    private String nev;
    private String tipus;
    private String sku;

    public Long getId() {
        return null;
    }
}
