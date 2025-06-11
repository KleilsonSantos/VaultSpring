package com.vaultspring.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class VaultController {

    @GetMapping("/app")
    ResponseEntity<Map<String, String>> get() {
        return ResponseEntity.ok(Map.of(
                "message", "Hello, Vault Spring!",
                "status", "running"
        ));
    }
}
