package org.api_sync.adapter.inbound.red;

import org.api_sync.adapter.inbound.request.ProveedorRequest;
import org.api_sync.adapter.outbound.entities.Proveedor;
import org.api_sync.services.suppliers.SupplierService;
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

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ProveedoresControllerTest {

    @Mock
    private SupplierService supplierService;

    @InjectMocks
    private ProveedoresController proveedoresController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createSupplier_ShouldReturnCreatedSupplier() {
        // Arrange
        ProveedorRequest request = new ProveedorRequest();
        request.setRazonSocial("Test Supplier");
        request.setCuit("12345678901");

        Proveedor expectedProveedor = new Proveedor();
        expectedProveedor.setId(1L);
        expectedProveedor.setRazonSocial("Test Supplier");
        expectedProveedor.setCuit("12345678901");

        when(supplierService.saveSupplier(any(ProveedorRequest.class))).thenReturn(expectedProveedor);

        // Act
        ResponseEntity<Proveedor> response = proveedoresController.createSupplier(request);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(expectedProveedor, response.getBody());
        verify(supplierService, times(1)).saveSupplier(request);
    }

    @Test
    void getAllSuppliers_ShouldReturnPageOfSuppliers() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 25);
        Proveedor proveedor = new Proveedor();
        proveedor.setId(1L);
        proveedor.setRazonSocial("Test Supplier");
        Page<Proveedor> expectedPage = new PageImpl<>(Collections.singletonList(proveedor));

        when(supplierService.getAllSuppliers(any(Pageable.class))).thenReturn(expectedPage);

        // Act
        ResponseEntity<Page<Proveedor>> response = proveedoresController.getAllSuppliers(pageable);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(expectedPage, response.getBody());
        verify(supplierService, times(1)).getAllSuppliers(pageable);
    }

    @Test
    void getSupplierById_ShouldReturnSupplier() {
        // Arrange
        Long supplierId = 1L;
        Proveedor expectedProveedor = new Proveedor();
        expectedProveedor.setId(supplierId);
        expectedProveedor.setRazonSocial("Test Supplier");

        when(supplierService.getSupplierById(supplierId)).thenReturn(expectedProveedor);

        // Act
        ResponseEntity<Proveedor> response = proveedoresController.getSupplierById(supplierId);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(expectedProveedor, response.getBody());
        verify(supplierService, times(1)).getSupplierById(supplierId);
    }

    @Test
    void deleteSupplier_ShouldReturnNoContent() {
        // Arrange
        Long supplierId = 1L;
        doNothing().when(supplierService).deleteSupplier(supplierId);

        // Act
        ResponseEntity<Void> response = proveedoresController.deleteSupplier(supplierId);

        // Assert
        assertNotNull(response);
        assertEquals(204, response.getStatusCode().value());
        verify(supplierService, times(1)).deleteSupplier(supplierId);
    }

    @Test
    void updateSupplier_ShouldReturnUpdatedSupplier() {
        // Arrange
        Long supplierId = 1L;
        ProveedorRequest request = new ProveedorRequest();
        request.setRazonSocial("Updated Supplier");
        request.setCuit("12345678901");

        Proveedor expectedProveedor = new Proveedor();
        expectedProveedor.setId(supplierId);
        expectedProveedor.setRazonSocial("Updated Supplier");
        expectedProveedor.setCuit("12345678901");

        when(supplierService.update(eq(supplierId), any(ProveedorRequest.class))).thenReturn(expectedProveedor);

        // Act
        ResponseEntity<Proveedor> response = proveedoresController.updateSupplier(supplierId, request);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(expectedProveedor, response.getBody());
        verify(supplierService, times(1)).update(supplierId, request);
    }
} 