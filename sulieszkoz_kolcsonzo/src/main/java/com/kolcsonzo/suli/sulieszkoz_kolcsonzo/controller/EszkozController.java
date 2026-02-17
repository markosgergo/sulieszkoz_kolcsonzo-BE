package com.kolcsonzo.suli.sulieszkoz_kolcsonzo.controller;

import com.kolcsonzo.suli.sulieszkoz_kolcsonzo.model.Eszkoz;
import com.kolcsonzo.suli.sulieszkoz_kolcsonzo.repository.EszkozRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/eszkozok")
public class EszkozController {

    private final EszkozRepository repo;

    public EszkozController(EszkozRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public List<Eszkoz> getAllEszkoz() {
        return repo.findAll();
    }
}