package org.api_sync.services.lista_precios;

import org.api_sync.services.lista_precios.dto.CvsDTO;
import org.api_sync.services.lista_precios.dto.ItemListaPreciosDTO;
import org.api_sync.services.lista_precios.dto.ListaPreciosDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for ExportPriceListService using Apache Commons CSV streaming.
 */
class ExportPriceListServiceTest {

    private ExportPriceListService service;
    private StubListaPreciosService stub;

    @BeforeEach
    void setUp() {
        stub = new StubListaPreciosService();
        service = new ExportPriceListService(stub);
    }

    @Test
    void generateCsv_returnsEmpty_whenListNotFound() {
        stub.setLista(Optional.empty());
        Optional<CvsDTO> result = service.generateCsv(123L);
        assertTrue(result.isEmpty(), "Expected empty Optional when list is not found");
    }

    @Test
    void generateCsv_writesAllRows_streaming() {
        ListaPreciosDTO dto = ListaPreciosDTO.builder()
                .id(1L)
                .nombre("Lista-Test")
                .items(Arrays.asList(
                        ItemListaPreciosDTO.builder().numero("A1").nombre("Articulo A").importe(new BigDecimal("10.50")).build(),
                        ItemListaPreciosDTO.builder().numero("B2").nombre("Articulo B").importe(new BigDecimal("99.99")).build()
                ))
                .build();
        stub.setLista(Optional.of(dto));

        Optional<CvsDTO> result = service.generateCsv(1L);
        assertTrue(result.isPresent(), "CSV should be generated when list exists");
        CvsDTO csv = result.get();

        // Validate name is propagated
        assertEquals("Lista-Test", csv.getNombre());

        String data = csv.getData();
        // Should contain two lines, one per item, no headers
        assertTrue(data.contains("A1,Articulo A,10.50"), "First row should be present");
        assertTrue(data.contains("B2,Articulo B,99.99"), "Second row should be present");
    }

    // Simple stub to isolate the unit under test
    static class StubListaPreciosService extends ListaPreciosService {
        private Optional<ListaPreciosDTO> lista = Optional.empty();

        StubListaPreciosService() { super(null, null, null, null, null, null, null); }

        void setLista(Optional<ListaPreciosDTO> lista) { this.lista = lista; }

        @Override
        public Optional<ListaPreciosDTO> getListaPrecio(Long id) {
            return lista;
        }
    }
}
