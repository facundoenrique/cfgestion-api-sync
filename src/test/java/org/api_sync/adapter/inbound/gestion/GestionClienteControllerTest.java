package org.api_sync.adapter.inbound.gestion;

import org.api_sync.adapter.inbound.gestion.request.GestionClienteRequest;
import org.api_sync.adapter.inbound.responses.ClienteResponse;
import org.api_sync.adapter.outbound.entities.gestion.GestionCliente;
import org.api_sync.services.gestion.clientes.GestionClienteService;
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

class GestionClienteControllerTest {

    @Mock
    private GestionClienteService clienteService;

    @InjectMocks
    private GestionClienteController clienteController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createCustomer_ShouldReturnCreatedCustomer() {
        // Arrange
        GestionClienteRequest request = new GestionClienteRequest();
        request.setRazonSocial("Test Company");
        request.setCondicionIva((short) 1);
        request.setEmpresa(1L);

        GestionCliente expectedCliente = new GestionCliente();
        expectedCliente.setId(1L);
        expectedCliente.setRazonSocial("Test Company");

        when(clienteService.saveCustomer(any(GestionClienteRequest.class))).thenReturn(expectedCliente);

        // Act
        ResponseEntity<GestionCliente> response = clienteController.createCustomer(request);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(expectedCliente, response.getBody());
        verify(clienteService, times(1)).saveCustomer(request);
    }

    @Test
    void getAllCustomers_ShouldReturnPageOfCustomers() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 25);
        GestionCliente cliente = new GestionCliente();
        cliente.setId(1L);
        cliente.setRazonSocial("Test Company");
        Page<GestionCliente> expectedPage = new PageImpl<>(Collections.singletonList(cliente));

        when(clienteService.getAllCustomers(any(Pageable.class))).thenReturn(expectedPage);

        // Act
        ResponseEntity<Page<GestionCliente>> response = clienteController.getAllCustomers(pageable);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(expectedPage, response.getBody());
        verify(clienteService, times(1)).getAllCustomers(pageable);
    }

    @Test
    void getCustomerById_ShouldReturnCustomer() {
        // Arrange
        Long customerId = 1L;
        ClienteResponse expectedResponse = ClienteResponse.builder()
                .id(customerId)
                .razonSocial("Test Company")
                .build();

        when(clienteService.getCustomerById(customerId)).thenReturn(expectedResponse);

        // Act
        ResponseEntity<ClienteResponse> response = clienteController.getCustomerById(customerId);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(expectedResponse, response.getBody());
        verify(clienteService, times(1)).getCustomerById(customerId);
    }

    @Test
    void deleteCustomer_ShouldReturnNoContent() {
        // Arrange
        Long customerId = 1L;
        doNothing().when(clienteService).deleteCustomer(customerId);

        // Act
        ResponseEntity<Void> response = clienteController.deleteCustomer(customerId);

        // Assert
        assertNotNull(response);
        assertEquals(204, response.getStatusCode().value());
        verify(clienteService, times(1)).deleteCustomer(customerId);
    }

    @Test
    void updateSupplier_ShouldReturnUpdatedCustomer() {
        // Arrange
        Long customerId = 1L;
        GestionClienteRequest request = new GestionClienteRequest();
        request.setRazonSocial("Updated Company");
        request.setCondicionIva((short) 1);
        request.setEmpresa(1L);

        GestionCliente expectedCliente = new GestionCliente();
        expectedCliente.setId(customerId);
        expectedCliente.setRazonSocial("Updated Company");

        when(clienteService.update(eq(customerId), any(GestionClienteRequest.class))).thenReturn(expectedCliente);

        // Act
        ResponseEntity<GestionCliente> response = clienteController.updateSupplier(customerId, request);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(expectedCliente, response.getBody());
        verify(clienteService, times(1)).update(customerId, request);
    }
} 