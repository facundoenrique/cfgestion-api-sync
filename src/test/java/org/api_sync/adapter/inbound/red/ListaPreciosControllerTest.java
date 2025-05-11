package org.api_sync.adapter.inbound.red;

import org.api_sync.adapter.inbound.request.ArticuloRequest;
import org.api_sync.adapter.inbound.request.ListaPreciosRequest;
import org.api_sync.adapter.outbound.entities.Proveedor;
import org.api_sync.services.articulos.dto.ArticuloDTO;
import org.api_sync.services.lista_precios.ListaPreciosService;
import org.api_sync.services.lista_precios.dto.ListaPreciosDTO;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ListaPreciosControllerTest {

    @Mock
    private ListaPreciosService listaPreciosService;

    @InjectMocks
    private ListaPreciosController listaPreciosController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void crearListaDePrecios_ShouldReturnCreatedLista() {
        // Arrange
        ListaPreciosRequest request = new ListaPreciosRequest();
        request.setNombre("Test List");
        request.setProveedor(1L);

        ListaPreciosDTO expectedLista = new ListaPreciosDTO();
        expectedLista.setId(1L);
        expectedLista.setNombre("Test List");
        expectedLista.setProveedor(Proveedor.builder().id(1L).build());

        when(listaPreciosService.crearListaDePrecios(any(ListaPreciosRequest.class))).thenReturn(expectedLista);

        // Act
        ResponseEntity<ListaPreciosDTO> response = listaPreciosController.crearListaDePrecios(request);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(expectedLista, response.getBody());
        verify(listaPreciosService, times(1)).crearListaDePrecios(request);
    }

    @Test
    void addItem_ShouldReturnAddedItem() {
        // Arrange
        Long listaId = 1L;
        ArticuloRequest articuloRequest = new ArticuloRequest();
        articuloRequest.setNombre("Test Article");
        articuloRequest.setNumero("123");

        ArticuloDTO expectedArticulo = new ArticuloDTO();
        expectedArticulo.setId(1L);
        expectedArticulo.setNombre("Test Article");
        expectedArticulo.setNumero("123");

        when(listaPreciosService.addItem(any(ArticuloRequest.class), eq(listaId))).thenReturn(expectedArticulo);

        // Act
        ResponseEntity<ArticuloDTO> response = listaPreciosController.addItem(listaId, articuloRequest);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(expectedArticulo, response.getBody());
        verify(listaPreciosService, times(1)).addItem(articuloRequest, listaId);
    }

    @Test
    void listarListasDePrecios_ShouldReturnPageOfListas() {
        // Arrange
        LocalDate fechaDesde = LocalDate.now().minusDays(7);
        LocalDate fechaHasta = LocalDate.now();
        Long proveedorId = 1L;
        String nombre = "Test List";
        Pageable pageable = PageRequest.of(0, 10);

        ListaPreciosDTO lista = new ListaPreciosDTO();
        lista.setId(1L);
        lista.setNombre("Test List");
        lista.setProveedor(Proveedor.builder().id(1L).build());
        Page<ListaPreciosDTO> expectedPage = new PageImpl<>(Collections.singletonList(lista));

        when(listaPreciosService.listarListasDePrecios(any(LocalDate.class), any(LocalDate.class), anyLong(), anyString(), any(Pageable.class)))
                .thenReturn(expectedPage);

        // Act
        ResponseEntity<Page<ListaPreciosDTO>> response = listaPreciosController.listarListasDePrecios(
                fechaDesde, fechaHasta, proveedorId, nombre, pageable);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(expectedPage, response.getBody());
        verify(listaPreciosService, times(1)).listarListasDePrecios(fechaDesde, fechaHasta, proveedorId, nombre, pageable);
    }

    @Test
    void getListaDePrecios_WhenExists_ShouldReturnLista() {
        // Arrange
        Long listaId = 1L;
        ListaPreciosDTO expectedLista = new ListaPreciosDTO();
        expectedLista.setId(listaId);
        expectedLista.setNombre("Test List");
        expectedLista.setProveedor(Proveedor.builder().id(1L).build());

        when(listaPreciosService.getListaPrecio(listaId)).thenReturn(Optional.of(expectedLista));

        // Act
        ResponseEntity<ListaPreciosDTO> response = listaPreciosController.getListaDePrecios(listaId);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(expectedLista, response.getBody());
        verify(listaPreciosService, times(1)).getListaPrecio(listaId);
    }

    @Test
    void getListaDePrecios_WhenNotExists_ShouldReturnNoContent() {
        // Arrange
        Long listaId = 1L;
        when(listaPreciosService.getListaPrecio(listaId)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<ListaPreciosDTO> response = listaPreciosController.getListaDePrecios(listaId);

        // Assert
        assertNotNull(response);
        assertEquals(204, response.getStatusCode().value());
        verify(listaPreciosService, times(1)).getListaPrecio(listaId);
    }
} 