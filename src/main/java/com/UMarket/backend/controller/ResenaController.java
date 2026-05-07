package com.UMarket.backend.controller;

import com.UMarket.backend.model.Resena;
import com.UMarket.backend.service.ResenaService;
import com.UMarket.backend.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/resenas")
public class ResenaController {

    @Autowired
    private ResenaService resenaService;

    @Autowired
    private UsuarioService usuarioService;

    // POST /api/resenas?vendedorId=2
    @PostMapping
    public ResponseEntity<?> crear(
            @RequestBody Resena resena,
            @RequestParam Long vendedorId) {
        try {
            Long autorId = getIdAutenticado();
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(resenaService.crear(resena, autorId, vendedorId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // GET /api/resenas/vendedor/1
    @GetMapping("/vendedor/{vendedorId}")
    public ResponseEntity<List<Resena>> obtenerPorVendedor(@PathVariable Long vendedorId) {
        return ResponseEntity.ok(resenaService.obtenerPorVendedor(vendedorId));
    }

    // GET /api/resenas/promedio/1
    @GetMapping("/promedio/{vendedorId}")
    public ResponseEntity<Double> obtenerPromedio(@PathVariable Long vendedorId) {
        return ResponseEntity.ok(resenaService.obtenerPromedio(vendedorId));
    }

    private Long getIdAutenticado() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return usuarioService.obtenerPorEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario autenticado no encontrado"))
                .getId();
    }
}
