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
    // GET /api/kolcsonzesek  – admin/alkalmazott lekérdezi a rendszerben lévő összes kölcsönzést
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ALKALMAZOTT')")
    public ResponseEntity<List<KolcsonzesDTO>> getAllKolcsonzes() {
        return ResponseEntity.ok(service.getAllKolcsonzes());
    }

    // GET /api/kolcsonzesek/kiadasra-var  – admin/alkalmazott látja a jóváhagyásra váró kérelmeket
    @GetMapping("/kiadasra-var")
    @PreAuthorize("hasAnyRole('ADMIN', 'ALKALMAZOTT')")
    public ResponseEntity<List<KolcsonzesDTO>> getKiadasraVarok() {
        return ResponseEntity.ok(service.getKiadasraVarokList());
    }

    //GET /api/kolcsonzesek/sajat
    @GetMapping("/sajat")
    public ResponseEntity<List<KolcsonzesDTO>> getSajatKolcsonzesek() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String bejelentkezettEmail = authentication.getName();
        return ResponseEntity.ok(service.getSajatKolcsonzesek(bejelentkezettEmail));
    }

    // GET /api/kolcsonzesek/kesesben
    @GetMapping("/kesesben")
    public ResponseEntity<List<KolcsonzesDTO>> getKesesbenLevoKolcsonzesek() {
        return ResponseEntity.ok(service.getKesesbenLevoKolcsonzesek());
    }

    // POST /api/kolcsonzesek  – a felhasználó lead egy új kölcsönzési/foglalási kérelmet egy eszközre
    @PostMapping
    public ResponseEntity<KolcsonzesDTO> createKolcsonzes(@Valid @RequestBody KolcsonzesLetrehozoDTO dto) {
        KolcsonzesDTO letrehozottKolcsonzes = service.createKolcsonzes(dto);
        return new ResponseEntity<>(letrehozottKolcsonzes, HttpStatus.CREATED);
    }

    // PUT /api/kolcsonzesek/{id}/elfogadas  – admin/alkalmazott jóváhagyja a kiadást
    @PutMapping("/{id}/elfogadas")
    @PreAuthorize("hasAnyRole('ADMIN', 'ALKALMAZOTT')")
    public ResponseEntity<KolcsonzesDTO> elfogadKiadasKerelem(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String kiadoEmail = authentication.getName();
        return ResponseEntity.ok(service.elfogadKiadasKerelemetEmail(id, kiadoEmail));
    }

    // DELETE /api/kolcsonzesek/{id}/elutasitas  – admin/alkalmazott elutasítja a kérelmet
    @DeleteMapping("/{id}/elutasitas")
    @PreAuthorize("hasAnyRole('ADMIN', 'ALKALMAZOTT')")
    public ResponseEntity<Void> elutasitKiadasKerelem(@PathVariable Long id) {
        service.elutasitKiadasKerelem(id);
        return ResponseEntity.noContent().build();
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