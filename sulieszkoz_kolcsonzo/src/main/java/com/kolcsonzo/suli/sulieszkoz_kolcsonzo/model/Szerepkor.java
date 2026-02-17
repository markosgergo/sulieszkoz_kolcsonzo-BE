package com.kolcsonzo.suli.sulieszkoz_kolcsonzo.model;

import com.kolcsonzo.suli.sulieszkoz_kolcsonzo.enums.FelhasznaloSzerepkor;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "szerepkor")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Szerepkor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "szerepkor_id")
    private Integer szerepkorId;

    @Column(name = "szerepkor_nev")
    private FelhasznaloSzerepkor szerepkorNev;

}
