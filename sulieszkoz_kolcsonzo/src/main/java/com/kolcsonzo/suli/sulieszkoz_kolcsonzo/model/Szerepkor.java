package com.kolcsonzo.suli.sulieszkoz_kolcsonzo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "szerepkor")
public class Szerepkor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "szerepkor_id")
    private Integer szerepkorId;

    @Column(name = "szerepkor_nev")
    private String szerepkorNev;

    public Integer getSzerepkorId() {
        return szerepkorId;
    }

    public void setSzerepkorId(Integer szerepkorId) {
        this.szerepkorId = szerepkorId;
    }

    public String getSzerepkorNev() {
        return szerepkorNev;
    }

    public void setSzerepkorNev(String szerepkorNev) {
        this.szerepkorNev = szerepkorNev;
    }
}
