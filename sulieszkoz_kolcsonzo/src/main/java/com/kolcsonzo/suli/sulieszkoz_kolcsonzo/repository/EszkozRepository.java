package com.kolcsonzo.suli.sulieszkoz_kolcsonzo.repository;

import com.kolcsonzo.suli.sulieszkoz_kolcsonzo.model.Eszkoz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List; // Ezt importáld be!

@Repository
public interface EszkozRepository extends JpaRepository<Eszkoz, Long> {

    List<Eszkoz> findByElerhetoTrue();
    List<Eszkoz> findByNevContainingIgnoreCase(String nev);

}