package com.kolcsonzo.suli.sulieszkoz_kolcsonzo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "eszkoz")
public class Eszkoz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "eszkoz_id")
    private Integer eszkozId;

    private String nev;
    private String tipus;
    private String sku;

    public Integer getEszkozId() {
        return eszkozId;
    }

    public void setEszkozId(Integer eszkozId) {
        this.eszkozId = eszkozId;
    }

    public String getNev() {
        return nev;
    }

    public void setNev(String nev) {
        this.nev = nev;
    }

    public String getTipus() {
        return tipus;
    }

    public void setTipus(String tipus) {
        this.tipus = tipus;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }
}
