package com.kolcsonzo.suli.sulieszkoz_kolcsonzo.controller;

import com.kolcsonzo.suli.sulieszkoz_kolcsonzo.dto.KolcsonzesDTO;
import com.kolcsonzo.suli.sulieszkoz_kolcsonzo.dto.KolcsonzesLetrehozoDTO;
import com.kolcsonzo.suli.sulieszkoz_kolcsonzo.service.KolcsonzesService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/kolcsonzesek")
public class KolcsonzesController {

    private final KolcsonzesService service;

    public KolcsonzesController(KolcsonzesService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<KolcsonzesDTO>> getAllKolcsonzes() {
        return ResponseEntity.ok(service.getAllKolcsonzes());
    }

    @PostMapping
    public ResponseEntity<KolcsonzesDTO> createKolcsonzes(@RequestBody KolcsonzesLetrehozoDTO dto) {
        KolcsonzesDTO letrehozottKolcsonzes = service.createKolcsonzes(dto);
        return new ResponseEntity<>(letrehozottKolcsonzes, HttpStatus.CREATED);
    }
}