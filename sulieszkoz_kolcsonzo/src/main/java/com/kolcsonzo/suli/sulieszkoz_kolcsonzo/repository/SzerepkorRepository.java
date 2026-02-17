package com.kolcsonzo.suli.sulieszkoz_kolcsonzo.repository;

import com.kolcsonzo.suli.sulieszkoz_kolcsonzo.model.Szerepkor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface SzerepkorRepository extends JpaRepository<Szerepkor,Long> {

}