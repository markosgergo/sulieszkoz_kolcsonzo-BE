package com.kolcsonzo.suli.sulieszkoz_kolcsonzo.repository;

import com.kolcsonzo.suli.sulieszkoz_kolcsonzo.model.Kolcsonzes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface KolcsonzesRepository extends JpaRepository<Kolcsonzes,Long> {

}