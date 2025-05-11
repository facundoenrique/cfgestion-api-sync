package org.api_sync.adapter.inbound.gestion;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class CheckControllerTest {

    private final CheckController checkController = new CheckController();

    @Test
    void get_ShouldReturnHelloWorld() {
        // Arrange
        String empresa = "test";
        Integer sucursal = 1;

        // Act
        String response = checkController.get(empresa, sucursal);

        // Assert
        assertNotNull(response);
        assertEquals("Hola mundo", response);
    }

    @Test
    void post_ShouldReturnEmptyString() {
        // Arrange
        String empresa = "test";
        Integer sucursal = 1;

        // Act
        String response = checkController.post(empresa, sucursal);

        // Assert
        assertNotNull(response);
        assertEquals("", response);
    }
} 