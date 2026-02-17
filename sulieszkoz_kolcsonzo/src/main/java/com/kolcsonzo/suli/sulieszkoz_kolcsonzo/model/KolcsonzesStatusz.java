package com.kolcsonzo.suli.sulieszkoz_kolcsonzo.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "KolcsonzesStatusz")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class KolcsonzesStatusz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "statusz_id")
    private Integer statuszId;

    @Column(name = "statusz_nev", length = 45)
    private String statuszNev;
}
