package org.api_sync.adapter.inbound.red;

import org.api_sync.adapter.inbound.request.ArticuloRequest;
import org.api_sync.services.articulos.ArticuloService;
import org.api_sync.services.articulos.PrecioService;
import org.api_sync.services.articulos.dto.ArticuloDTO;
import org.api_sync.services.articulos.dto.PrecioDTO;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class ArticulosControllerTest {

    @Mock
    private ArticuloService articuloService;

    @Mock
    private PrecioService precioService;

    @InjectMocks
    private ArticulosController articulosController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void get_ShouldReturnArticulo() {
        // Arrange
        Long articuloId = 1L;
        ArticuloDTO expectedArticulo = new ArticuloDTO();
        expectedArticulo.setId(articuloId);
        expectedArticulo.setNombre("Test Article");
        expectedArticulo.setNumero("123");

        when(articuloService.get(articuloId)).thenReturn(expectedArticulo);

        // Act
        ResponseEntity<ArticuloDTO> response = articulosController.get(articuloId);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(expectedArticulo, response.getBody());
        verify(articuloService, times(1)).get(articuloId);
    }

    @Test
    void actualizarArticulo_ShouldReturnUpdatedArticulo() {
        // Arrange
        Long articuloId = 1L;
        ArticuloRequest request = new ArticuloRequest();
        request.setNombre("Updated Article");
        request.setNumero("123");

        ArticuloDTO expectedArticulo = new ArticuloDTO();
        expectedArticulo.setId(articuloId);
        expectedArticulo.setNombre("Updated Article");
        expectedArticulo.setNumero("123");

        when(articuloService.actualizarArticulo(eq(articuloId), any(ArticuloRequest.class))).thenReturn(expectedArticulo);

        // Act
        ResponseEntity<ArticuloDTO> response = articulosController.actualizarArticulo(articuloId, request);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(expectedArticulo, response.getBody());
        verify(articuloService, times(1)).actualizarArticulo(articuloId, request);
    }

    @Test
    void search_WithValidParameters_ShouldReturnPageOfArticulos() {
        // Arrange
        String numero = "123";
        String nombre = "Test";
        Pageable pageable = PageRequest.of(0, 25);
        ArticuloDTO articulo = new ArticuloDTO();
        articulo.setId(1L);
        articulo.setNombre("Test Article");
        articulo.setNumero("123");
        Page<ArticuloDTO> expectedPage = new PageImpl<>(Collections.singletonList(articulo));

        when(articuloService.search(numero, nombre, pageable)).thenReturn(expectedPage);

        // Act
        ResponseEntity<Page<ArticuloDTO>> response = articulosController.search(numero, nombre, pageable);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(expectedPage, response.getBody());
        verify(articuloService, times(1)).search(numero, nombre, pageable);
    }

    @Test
    void listarArticulos_WithoutNumero_ShouldReturnAllArticulos() {
        // Arrange
        List<ArticuloDTO> expectedArticulos = Collections.singletonList(new ArticuloDTO());
        when(articuloService.listarArticulos()).thenReturn(expectedArticulos);

        // Act
        ResponseEntity<List<ArticuloDTO>> response = articulosController.listarArticulos(null);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(expectedArticulos, response.getBody());
        verify(articuloService, times(1)).listarArticulos();
    }

    @Test
    void listarArticulos_WithNumero_ShouldReturnSingleArticulo() {
        // Arrange
        String numero = "123";
        ArticuloDTO expectedArticulo = new ArticuloDTO();
        expectedArticulo.setNumero(numero);
        when(articuloService.getItem(numero)).thenReturn(expectedArticulo);

        // Act
        ResponseEntity<List<ArticuloDTO>> response = articulosController.listarArticulos(numero);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(Collections.singletonList(expectedArticulo), response.getBody());
        verify(articuloService, times(1)).getItem(numero);
    }

    @Test
    void obtenerPrecioVigente_ShouldReturnPrecio() {
        // Arrange
        Long articuloId = 1L;
        PrecioDTO expectedPrecio = PrecioDTO.builder()
                .id(1L)
                .articuloId(articuloId)
                .importe(new BigDecimal("100.00"))
                .fechaVigencia(LocalDate.now())
                .build();

        when(precioService.obtenerPrecioVigente(articuloId)).thenReturn(expectedPrecio);

        // Act
        ResponseEntity<PrecioDTO> response = articulosController.obtenerPrecioVigente(articuloId);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(expectedPrecio, response.getBody());
        verify(precioService, times(1)).obtenerPrecioVigente(articuloId);
    }
} 