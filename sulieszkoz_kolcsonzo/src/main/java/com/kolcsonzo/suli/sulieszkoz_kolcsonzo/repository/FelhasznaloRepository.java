package com.kolcsonzo.suli.sulieszkoz_kolcsonzo.repository;

import com.kolcsonzo.suli.sulieszkoz_kolcsonzo.model.Felhasznalo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FelhasznaloRepository extends JpaRepository<Felhasznalo,Long> {
    Optional<Felhasznalo> findByEmail(String email);
}