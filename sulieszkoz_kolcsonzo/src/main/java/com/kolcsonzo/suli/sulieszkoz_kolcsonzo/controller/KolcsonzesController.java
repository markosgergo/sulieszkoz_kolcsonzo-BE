package com.kolcsonzo.suli.sulieszkoz_kolcsonzo.controller;

import com.kolcsonzo.suli.sulieszkoz_kolcsonzo.dto.KolcsonzesDTO;
import com.kolcsonzo.suli.sulieszkoz_kolcsonzo.dto.KolcsonzesLetrehozoDTO;
import com.kolcsonzo.suli.sulieszkoz_kolcsonzo.service.KolcsonzesService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    @PreAuthorize("hasAnyRole('ADMIN', 'ALKALMAZOTT')")
    public ResponseEntity<List<KolcsonzesDTO>> getAllKolcsonzes() {
        return ResponseEntity.ok(service.getAllKolcsonzes());
    }

    //GET /api/kolcsonzesek/sajat
    @GetMapping("/sajat")
    public ResponseEntity<List<KolcsonzesDTO>> getSajatKolcsonzesek() {
        // 1. Kinyerjük a bejelentkezett felhasználó adatait a biztonsági kontextusból
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 2. A név (username) nálunk az Email címet jelenti (hiszen azzal jelentkezett be)
        String bejelentkezettEmail = authentication.getName();

        // 3. Visszaadjuk az ő kölcsönzéseit
        return ResponseEntity.ok(service.getSajatKolcsonzesek(bejelentkezettEmail));
    }

    // GET /api/kolcsonzesek/kesesben
    @GetMapping("/kesesben")
    public ResponseEntity<List<KolcsonzesDTO>> getKesesbenLevoKolcsonzesek() {
        return ResponseEntity.ok(service.getKesesbenLevoKolcsonzesek());
    }

    @PostMapping
    public ResponseEntity<KolcsonzesDTO> createKolcsonzes(@Valid @RequestBody KolcsonzesLetrehozoDTO dto) {
        KolcsonzesDTO letrehozottKolcsonzes = service.createKolcsonzes(dto);
        return new ResponseEntity<>(letrehozottKolcsonzes, HttpStatus.CREATED);
    }

    //PUT /api/kolcsonzesek/{id}/visszavetel
    @PutMapping("/{id}/visszavetel")
    public ResponseEntity<KolcsonzesDTO> visszaveszKolcsonzes(@PathVariable Long id) {
        return ResponseEntity.ok(service.visszaveszKolcsonzes(id));
    }

    // PUT /api/kolcsonzesek/visszavetel/eszkoz/{eszkozId}
    @PutMapping("/visszavetel/eszkoz/{eszkozId}")
    public ResponseEntity<KolcsonzesDTO> visszaveszByEszkozId(@PathVariable Long eszkozId) {
        return ResponseEntity.ok(service.visszaveszByEszkozId(eszkozId));
    }

}