package org.api_sync.adapter.inbound.gestion;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping("/auth-test")
    public ResponseEntity<?> testAuth(Principal principal) {
        Map<String, String> response = new HashMap<>();
        response.put("message", "¡Autenticación exitosa!");
        response.put("username", principal.getName());
        return ResponseEntity.ok(response);
    }
} 