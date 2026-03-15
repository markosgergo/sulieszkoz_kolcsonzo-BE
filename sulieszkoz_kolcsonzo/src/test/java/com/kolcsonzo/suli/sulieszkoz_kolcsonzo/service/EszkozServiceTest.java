package com.kolcsonzo.suli.sulieszkoz_kolcsonzo.service;

import com.kolcsonzo.suli.sulieszkoz_kolcsonzo.dto.EszkozDTO;
import com.kolcsonzo.suli.sulieszkoz_kolcsonzo.exception.EntityNotFoundException;
import com.kolcsonzo.suli.sulieszkoz_kolcsonzo.mapper.EszkozMapper;
import com.kolcsonzo.suli.sulieszkoz_kolcsonzo.model.Eszkoz;
import com.kolcsonzo.suli.sulieszkoz_kolcsonzo.repository.EszkozRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EszkozServiceTest {

    @Mock
    private EszkozRepository repository;

    @Mock
    private EszkozMapper mapper;

    @InjectMocks
    private EszkozService service;

    private Eszkoz eszkoz;
    private EszkozDTO eszkozDTO;

    @BeforeEach
    void setUp() {
        eszkoz = new Eszkoz();
        eszkoz.setEszkozId(1L);
        eszkoz.setNev("Laptop");
        eszkoz.setTipus("Elektronika");
        eszkoz.setSku("LAP-001");
        eszkoz.setElerheto(true);

        eszkozDTO = new EszkozDTO(1L, "Laptop", "Elektronika", "LAP-001", null, true);
    }

    @Test
    void getAllEszkoz_visszaadjaAzOsszetEszkozt() {
        when(repository.findAll()).thenReturn(List.of(eszkoz));
        when(mapper.toDTO(eszkoz)).thenReturn(eszkozDTO);

        List<EszkozDTO> eredmeny = service.getAllEszkoz();

        assertThat(eredmeny).hasSize(1);
        assertThat(eredmeny.get(0).getNev()).isEqualTo("Laptop");
    }

    @Test
    void getEszkozById_letezőId_visszaadjaAzEszkozt() {
        when(repository.findById(1L)).thenReturn(Optional.of(eszkoz));
        when(mapper.toDTO(eszkoz)).thenReturn(eszkozDTO);

        EszkozDTO eredmeny = service.getEszkozById(1L);

        assertThat(eredmeny.getId()).isEqualTo(1L);
        assertThat(eredmeny.getNev()).isEqualTo("Laptop");
    }

    @Test
    void getEszkozById_nemLetezoId_EntityNotFoundExceptiontDob() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getEszkozById(99L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void createEszkoz_elmentesUtanVisszaadjaAzEszkozt() {
        when(repository.save(any(Eszkoz.class))).thenReturn(eszkoz);
        when(mapper.toDTO(eszkoz)).thenReturn(eszkozDTO);

        EszkozDTO eredmeny = service.createEszkoz(eszkozDTO);

        assertThat(eredmeny.getNev()).isEqualTo("Laptop");
        verify(repository, times(1)).save(any(Eszkoz.class));
    }

    @Test
    void deleteEszkoz_letezőId_torlesHivodik() {
        when(repository.existsById(1L)).thenReturn(true);

        service.deleteEszkoz(1L);

        verify(repository, times(1)).deleteById(1L);
    }

    @Test
    void deleteEszkoz_nemLetezoId_EntityNotFoundExceptiontDob() {
        when(repository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> service.deleteEszkoz(99L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("99");

        verify(repository, never()).deleteById(any());
    }

    @Test
    void getSzabadEszkozok_csakElerheto_eszkoztAdVissza() {
        when(repository.findByElerhetoTrue()).thenReturn(List.of(eszkoz));
        when(mapper.toDTO(eszkoz)).thenReturn(eszkozDTO);

        List<EszkozDTO> eredmeny = service.getSzabadEszkozok();

        assertThat(eredmeny).hasSize(1);
        assertThat(eredmeny.get(0).isElerheto()).isTrue();
    }
}