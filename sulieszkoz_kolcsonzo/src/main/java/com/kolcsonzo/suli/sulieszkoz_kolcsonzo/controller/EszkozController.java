package com.kolcsonzo.suli.sulieszkoz_kolcsonzo.controller;

import com.kolcsonzo.suli.sulieszkoz_kolcsonzo.dto.EszkozDTO;
import com.kolcsonzo.suli.sulieszkoz_kolcsonzo.service.EszkozService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/eszkozok")
public class EszkozController {
    private final EszkozService service;

    public EszkozController(EszkozService service) {
        this.service = service;
    }

    // GET: /api/eszkozok Összes eszköz lekérdezése
    @GetMapping
    public ResponseEntity<List<EszkozDTO>> getAllEszkoz() {
        return ResponseEntity.ok(service.getAllEszkoz());
    }

    // GET: /api/eszkozok/1 Egy adott eszköz lekérdezése ID alapján
    @GetMapping("/{id}")
    public ResponseEntity<EszkozDTO> getEszkozById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getEszkozById(id));
    }

    // POST: /api/eszkozok Új eszköz hozzáadása
    @PostMapping
    public ResponseEntity<EszkozDTO> createEszkoz(@RequestBody EszkozDTO dto) {
        EszkozDTO letrehozottEszkoz = service.createEszkoz(dto);
        return new ResponseEntity<>(letrehozottEszkoz, HttpStatus.CREATED); // 201 Created státuszkód
    }

    // PUT: /api/eszkozok/1 Meglévő eszköz frissítése
    @PutMapping("/{id}")
    public ResponseEntity<EszkozDTO> updateEszkoz(@PathVariable Long id, @RequestBody EszkozDTO dto) {
        return ResponseEntity.ok(service.updateEszkoz(id, dto));
    }

    // DELETE: /api/eszkozok/1 Eszköz törlése
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEszkoz(@PathVariable Long id) {
        service.deleteEszkoz(id);
        return ResponseEntity.noContent().build();
    }
}