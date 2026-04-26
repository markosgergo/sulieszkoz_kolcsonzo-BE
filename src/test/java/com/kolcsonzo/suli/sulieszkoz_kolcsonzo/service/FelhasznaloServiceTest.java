package com.kolcsonzo.suli.sulieszkoz_kolcsonzo.service;

import com.kolcsonzo.suli.sulieszkoz_kolcsonzo.dto.FelhasznaloDTO;
import com.kolcsonzo.suli.sulieszkoz_kolcsonzo.dto.FelhasznaloLetrehozoDTO;
import com.kolcsonzo.suli.sulieszkoz_kolcsonzo.enums.FelhasznaloSzerepkor;
import com.kolcsonzo.suli.sulieszkoz_kolcsonzo.exception.EntityNotFoundException;
import com.kolcsonzo.suli.sulieszkoz_kolcsonzo.mapper.FelhasznaloMapper;
import com.kolcsonzo.suli.sulieszkoz_kolcsonzo.model.Felhasznalo;
import com.kolcsonzo.suli.sulieszkoz_kolcsonzo.model.Szerepkor;
import com.kolcsonzo.suli.sulieszkoz_kolcsonzo.repository.FelhasznaloRepository;
import com.kolcsonzo.suli.sulieszkoz_kolcsonzo.repository.SzerepkorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FelhasznaloServiceTest {

    @Mock
    private FelhasznaloRepository felhasznaloRepository;

    @Mock
    private SzerepkorRepository szerepkorRepository;

    @Mock
    private FelhasznaloMapper mapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private FelhasznaloService service;

    private Felhasznalo felhasznalo;
    private FelhasznaloDTO felhasznaloDTO;
    private Szerepkor szerepkor;

    @BeforeEach
    void setUp() {
        szerepkor = new Szerepkor();
        szerepkor.setSzerepkorId(1L);
        szerepkor.setSzerepkorNev(FelhasznaloSzerepkor.FELHASZNALO);

        felhasznalo = new Felhasznalo();
        felhasznalo.setFelhasznaloId(1L);
        felhasznalo.setNev("Teszt Elek");
        felhasznalo.setEmail("teszt@example.com");
        felhasznalo.setJelszo("titkositott_jelszo");
        felhasznalo.setSzerepkor(szerepkor);

        felhasznaloDTO = new FelhasznaloDTO();
        felhasznaloDTO.setId(1L);
        felhasznaloDTO.setNev("Teszt Elek");
        felhasznaloDTO.setEmail("teszt@example.com");
        felhasznaloDTO.setSzerepkorNev("FELHASZNALO");
    }

    @Test
    void getAllFelhasznalo_visszaadjaAzOsszesfelhasznalot() {
        when(felhasznaloRepository.findAll()).thenReturn(List.of(felhasznalo));
        when(mapper.toDTO(felhasznalo)).thenReturn(felhasznaloDTO);

        List<FelhasznaloDTO> eredmeny = service.getAllFelhasznalo();

        assertThat(eredmeny).hasSize(1);
        assertThat(eredmeny.get(0).getEmail()).isEqualTo("teszt@example.com");
    }

    @Test
    void getFelhasznaloById_letezőId_visszaadjaAFelhasznalot() {
        when(felhasznaloRepository.findById(1L)).thenReturn(Optional.of(felhasznalo));
        when(mapper.toDTO(felhasznalo)).thenReturn(felhasznaloDTO);

        FelhasznaloDTO eredmeny = service.getFelhasznaloById(1L);

        assertThat(eredmeny.getNev()).isEqualTo("Teszt Elek");
    }

    @Test
    void getFelhasznaloById_nemLetezoId_EntityNotFoundExceptiontDob() {
        when(felhasznaloRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getFelhasznaloById(99L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void getFelhasznaloByEmail_letezőEmail_visszaadjaAFelhasznalot() {
        when(felhasznaloRepository.findByEmail("teszt@example.com")).thenReturn(Optional.of(felhasznalo));
        when(mapper.toDTO(felhasznalo)).thenReturn(felhasznaloDTO);

        FelhasznaloDTO eredmeny = service.getFelhasznaloByEmail("teszt@example.com");

        assertThat(eredmeny.getEmail()).isEqualTo("teszt@example.com");
    }

    @Test
    void getFelhasznaloByEmail_nemLetezoEmail_EntityNotFoundExceptiontDob() {
        when(felhasznaloRepository.findByEmail("nem@letezik.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getFelhasznaloByEmail("nem@letezik.com"))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("nem@letezik.com");
    }

    @Test
    void createFelhasznalo_ujFelhasznalo_jelszotTitkositvaElmenti() {
        FelhasznaloLetrehozoDTO dto = new FelhasznaloLetrehozoDTO();
        dto.setNev("Teszt Elek");
        dto.setEmail("teszt@example.com");
        dto.setJelszo("jelszo123");
        dto.setSzerepkorNev("FELHASZNALO");

        when(passwordEncoder.encode("jelszo123")).thenReturn("titkositott_jelszo");
        when(szerepkorRepository.findBySzerepkorNev(FelhasznaloSzerepkor.FELHASZNALO))
                .thenReturn(Optional.of(szerepkor));
        when(felhasznaloRepository.save(any(Felhasznalo.class))).thenReturn(felhasznalo);
        when(mapper.toDTO(felhasznalo)).thenReturn(felhasznaloDTO);

        FelhasznaloDTO eredmeny = service.createFelhasznalo(dto);

        assertThat(eredmeny.getNev()).isEqualTo("Teszt Elek");
        // Ellenorizzuk, hogy a jelszot titkositva mentette-e el
        verify(passwordEncoder, times(1)).encode("jelszo123");
        verify(felhasznaloRepository, times(1)).save(any(Felhasznalo.class));
    }

    @Test
    void createFelhasznalo_nemLetezoSzerepkor_EntityNotFoundExceptiontDob() {
        FelhasznaloLetrehozoDTO dto = new FelhasznaloLetrehozoDTO();
        dto.setNev("Teszt Elek");
        dto.setEmail("teszt@example.com");
        dto.setJelszo("jelszo123");
        dto.setSzerepkorNev("NEMLETEZIK");

        assertThatThrownBy(() -> service.createFelhasznalo(dto))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void deleteFelhasznalo_letezőId_torlesHivodik() {
        when(felhasznaloRepository.existsById(1L)).thenReturn(true);

        service.deleteFelhasznalo(1L);

        verify(felhasznaloRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteFelhasznalo_nemLetezoId_EntityNotFoundExceptiontDob() {
        when(felhasznaloRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> service.deleteFelhasznalo(99L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("99");

        verify(felhasznaloRepository, never()).deleteById(any());
    }
}