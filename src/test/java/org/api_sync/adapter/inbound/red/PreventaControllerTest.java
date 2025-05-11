package org.api_sync.adapter.inbound.red;

import org.api_sync.adapter.inbound.responses.PreventaResponseDTO;
import org.api_sync.services.proposals.PreventaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PreventaControllerTest {

    @Mock
    private PreventaService preventaService;

    @InjectMocks
    private PreventaController preventaController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findAll_ShouldReturnPageOfPreventas() {
        // Arrange
        LocalDate fechaDesde = LocalDate.now().minusDays(7);
        LocalDate fechaHasta = LocalDate.now();
        Long proveedorId = 1L;
        String nombre = "Test Preventa";
        Pageable pageable = PageRequest.of(0, 10);

        PreventaResponseDTO preventa = new PreventaResponseDTO();
        preventa.setId(1L);
        preventa.setNombre("Test Preventa");
        Page<PreventaResponseDTO> expectedPage = new PageImpl<>(Collections.singletonList(preventa));

        when(preventaService.listar(any(LocalDate.class), any(LocalDate.class), anyLong(), anyString(), any(Pageable.class)))
                .thenReturn(expectedPage);

        // Act
        ResponseEntity<Page<PreventaResponseDTO>> response = preventaController.findAll(
                fechaDesde, fechaHasta, proveedorId, nombre, pageable);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(expectedPage, response.getBody());
        verify(preventaService, times(1)).listar(fechaDesde, fechaHasta, proveedorId, nombre, pageable);
    }

    @Test
    void findAll_WithNullParameters_ShouldReturnPageOfPreventas() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        PreventaResponseDTO preventa = new PreventaResponseDTO();
        preventa.setId(1L);
        preventa.setNombre("Test Preventa");
        Page<PreventaResponseDTO> expectedPage = new PageImpl<>(Collections.singletonList(preventa));

        when(preventaService.listar(any(), any(), any(), any(), any(Pageable.class)))
                .thenReturn(expectedPage);

        // Act
        ResponseEntity<Page<PreventaResponseDTO>> response = preventaController.findAll(
                null, null, null, null, pageable);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(expectedPage, response.getBody());
        verify(preventaService, times(1)).listar(null, null, null, null, pageable);
    }
} 