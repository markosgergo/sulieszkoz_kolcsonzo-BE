package com.kolcsonzo.suli.sulieszkoz_kolcsonzo.controller;

import com.kolcsonzo.suli.sulieszkoz_kolcsonzo.dto.FelhasznaloLetrehozoDTO;
import com.kolcsonzo.suli.sulieszkoz_kolcsonzo.dto.FelhasznaloDTO;
import com.kolcsonzo.suli.sulieszkoz_kolcsonzo.service.FelhasznaloService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/felhasznalok")
public class FelhasznaloController {

    private final FelhasznaloService service;

    public FelhasznaloController(FelhasznaloService service) {
        this.service = service;
    }

    // GET: /api/felhasznalok Összes felhasználó listázása
    @GetMapping
    public ResponseEntity<List<FelhasznaloDTO>> getAllFelhasznalok() {
        return ResponseEntity.ok(service.getAllFelhasznalo());
    }

    // GET: /api/felhasznalok/1 Egy felhasználó adatainak lekérése
    @GetMapping("/{id}")
    public ResponseEntity<FelhasznaloDTO> getFelhasznaloById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getFelhasznaloById(id));
    }

    // POST: /api/felhasznalok Új felhasználó regisztrációja
    @PostMapping
    public ResponseEntity<FelhasznaloDTO> createFelhasznalo(@RequestBody FelhasznaloLetrehozoDTO dto) {
        FelhasznaloDTO letrehozottFelhasznalo = service.createFelhasznalo(dto);
        return new ResponseEntity<>(letrehozottFelhasznalo, HttpStatus.CREATED);
    }

    // DELETE: /api/felhasznalok/1 Felhasználó törlése
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFelhasznalo(@PathVariable Long id) {
        service.deleteFelhasznalo(id);
        return ResponseEntity.noContent().build();
    }
}