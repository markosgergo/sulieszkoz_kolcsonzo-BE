package com.kolcsonzo.suli.sulieszkoz_kolcsonzo.service;

import com.kolcsonzo.suli.sulieszkoz_kolcsonzo.dto.KolcsonzesDTO;
import com.kolcsonzo.suli.sulieszkoz_kolcsonzo.dto.KolcsonzesLetrehozoDTO;
import com.kolcsonzo.suli.sulieszkoz_kolcsonzo.enums.KolcsonzesStatuszEnum;
import com.kolcsonzo.suli.sulieszkoz_kolcsonzo.exception.BusinessException;
import com.kolcsonzo.suli.sulieszkoz_kolcsonzo.exception.EntityNotFoundException;
import com.kolcsonzo.suli.sulieszkoz_kolcsonzo.mapper.KolcsonzesMapper;
import com.kolcsonzo.suli.sulieszkoz_kolcsonzo.model.Eszkoz;
import com.kolcsonzo.suli.sulieszkoz_kolcsonzo.model.Felhasznalo;
import com.kolcsonzo.suli.sulieszkoz_kolcsonzo.model.Kolcsonzes;
import com.kolcsonzo.suli.sulieszkoz_kolcsonzo.repository.EszkozRepository;
import com.kolcsonzo.suli.sulieszkoz_kolcsonzo.repository.FelhasznaloRepository;
import com.kolcsonzo.suli.sulieszkoz_kolcsonzo.repository.KolcsonzesRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KolcsonzesServiceTest {

    @Mock
    private KolcsonzesRepository kolcsonzesRepository;

    @Mock
    private FelhasznaloRepository felhasznaloRepository;

    @Mock
    private EszkozRepository eszkozRepository;

    @Mock
    private KolcsonzesMapper mapper;

    @InjectMocks
    private KolcsonzesService service;

    private Felhasznalo diak;
    private Felhasznalo kiado;
    private Eszkoz eszkoz;
    private Kolcsonzes kolcsonzes;
    private KolcsonzesDTO kolcsonzesDTO;

    @BeforeEach
    void setUp() {
        diak = new Felhasznalo();
        diak.setFelhasznaloId(1L);
        diak.setNev("Diak Janos");
        diak.setEmail("diak@iskola.hu");

        kiado = new Felhasznalo();
        kiado.setFelhasznaloId(2L);
        kiado.setNev("Tanar Bela");

        eszkoz = new Eszkoz();
        eszkoz.setEszkozId(1L);
        eszkoz.setNev("Laptop");
        eszkoz.setElerheto(true);

        kolcsonzes = new Kolcsonzes();
        kolcsonzes.setKolcsonzesId(1L);
        kolcsonzes.setFelhasznalo(diak);
        kolcsonzes.setEszkoz(eszkoz);
        kolcsonzes.setKiado(kiado);
        kolcsonzes.setStatusz(KolcsonzesStatuszEnum.KIKOLCSONOZVE);
        kolcsonzes.setKiadasDatuma(LocalDateTime.now());
        kolcsonzes.setHatarido(LocalDate.now().plusDays(7));

        kolcsonzesDTO = new KolcsonzesDTO();
        kolcsonzesDTO.setId(1L);
        kolcsonzesDTO.setStatuszNev("KIKOLCSONOZVE");
    }

    @Test
    void getAllKolcsonzes_visszaadjaAzOsszetKolcsonzest() {
        when(kolcsonzesRepository.findAll()).thenReturn(List.of(kolcsonzes));
        when(mapper.toDTO(kolcsonzes)).thenReturn(kolcsonzesDTO);

        List<KolcsonzesDTO> eredmeny = service.getAllKolcsonzes();

        assertThat(eredmeny).hasSize(1);
    }

    @Test
    void getSajatKolcsonzesek_emailAlapjan_visszaadjaAHelyes() {
        when(kolcsonzesRepository.findByFelhasznalo_Email("diak@iskola.hu"))
                .thenReturn(List.of(kolcsonzes));
        when(mapper.toDTO(kolcsonzes)).thenReturn(kolcsonzesDTO);

        List<KolcsonzesDTO> eredmeny = service.getSajatKolcsonzesek("diak@iskola.hu");

        assertThat(eredmeny).hasSize(1);
        verify(kolcsonzesRepository).findByFelhasznalo_Email("diak@iskola.hu");
    }

    @Test
    void createKolcsonzes_sikeresLétrehozas_elerheto_hamissaValik() {
        KolcsonzesLetrehozoDTO dto = new KolcsonzesLetrehozoDTO();
        dto.setFelhasznaloId(1L);
        dto.setKiadoId(2L);
        dto.setEszkozId(1L);
        dto.setHatarido(LocalDate.now().plusDays(7));

        when(felhasznaloRepository.findById(1L)).thenReturn(Optional.of(diak));
        when(felhasznaloRepository.findById(2L)).thenReturn(Optional.of(kiado));
        when(eszkozRepository.findById(1L)).thenReturn(Optional.of(eszkoz));
        when(kolcsonzesRepository.save(any(Kolcsonzes.class))).thenReturn(kolcsonzes);
        when(mapper.toDTO(kolcsonzes)).thenReturn(kolcsonzesDTO);

        KolcsonzesDTO eredmeny = service.createKolcsonzes(dto);

        assertThat(eredmeny).isNotNull();
        // Az eszkoz elerhetosege hamisra allitodott
        assertThat(eszkoz.isElerheto()).isFalse();
        verify(eszkozRepository, times(1)).save(eszkoz);
        verify(kolcsonzesRepository, times(1)).save(any(Kolcsonzes.class));
    }

    @Test
    void createKolcsonzes_nemElerhetoEszkoz_BusinessExceptiontDob() {
        eszkoz.setElerheto(false);

        KolcsonzesLetrehozoDTO dto = new KolcsonzesLetrehozoDTO();
        dto.setFelhasznaloId(1L);
        dto.setKiadoId(2L);
        dto.setEszkozId(1L);
        dto.setHatarido(LocalDate.now().plusDays(7));

        when(felhasznaloRepository.findById(1L)).thenReturn(Optional.of(diak));
        when(felhasznaloRepository.findById(2L)).thenReturn(Optional.of(kiado));
        when(eszkozRepository.findById(1L)).thenReturn(Optional.of(eszkoz));

        assertThatThrownBy(() -> service.createKolcsonzes(dto))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("keszleten");

        // Biztositsuk, hogy a kolcsonzes nem lett elmentve
        verify(kolcsonzesRepository, never()).save(any());
    }

    @Test
    void createKolcsonzes_nemLetezoFelhasznalo_EntityNotFoundExceptiontDob() {
        KolcsonzesLetrehozoDTO dto = new KolcsonzesLetrehozoDTO();
        dto.setFelhasznaloId(99L);
        dto.setKiadoId(2L);
        dto.setEszkozId(1L);
        dto.setHatarido(LocalDate.now().plusDays(7));

        when(felhasznaloRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.createKolcsonzes(dto))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void visszaveszKolcsonzes_sikeresVisszavetel_eszközElérhetőLesz() {
        when(kolcsonzesRepository.findById(1L)).thenReturn(Optional.of(kolcsonzes));
        when(kolcsonzesRepository.save(any(Kolcsonzes.class))).thenReturn(kolcsonzes);
        when(mapper.toDTO(kolcsonzes)).thenReturn(kolcsonzesDTO);

        service.visszaveszKolcsonzes(1L);

        assertThat(kolcsonzes.getStatusz()).isEqualTo(KolcsonzesStatuszEnum.VISSZAADVA);
        assertThat(kolcsonzes.getVisszavetelDatuma()).isNotNull();
        // Az eszkoz ujra elerhetove valt
        assertThat(eszkoz.isElerheto()).isTrue();
        verify(eszkozRepository, times(1)).save(eszkoz);
    }

    @Test
    void visszaveszKolcsonzes_marVisszaadott_BusinessExceptiontDob() {
        kolcsonzes.setStatusz(KolcsonzesStatuszEnum.VISSZAADVA);
        when(kolcsonzesRepository.findById(1L)).thenReturn(Optional.of(kolcsonzes));

        assertThatThrownBy(() -> service.visszaveszKolcsonzes(1L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("vissza lett adva");

        verify(kolcsonzesRepository, never()).save(any());
    }

    @Test
    void visszaveszKolcsonzes_nemLetezoId_EntityNotFoundExceptiontDob() {
        when(kolcsonzesRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.visszaveszKolcsonzes(99L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("99");
    }
}