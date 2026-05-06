package com.UMarket.backend.controller;

import com.UMarket.backend.model.Publicacion;
import com.UMarket.backend.service.PublicacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/publicaciones")
@CrossOrigin(origins = "*")
public class PublicacionController {

    @Autowired
    private PublicacionService publicacionService;

    // POST /api/publicaciones?vendedorId=1
    @PostMapping
    public ResponseEntity<?> crear(
            @RequestBody Publicacion publicacion,
            @RequestParam Long vendedorId) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(publicacionService.crear(publicacion, vendedorId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // GET /api/publicaciones — el feed principal
    @GetMapping
    public ResponseEntity<List<Publicacion>> obtenerFeed() {
        return ResponseEntity.ok(publicacionService.obtenerFeed());
    }

    // GET /api/publicaciones/1
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        return publicacionService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET /api/publicaciones/vendedor/1
    @GetMapping("/vendedor/{vendedorId}")
    public ResponseEntity<List<Publicacion>> obtenerPorVendedor(
            @PathVariable Long vendedorId) {
        return ResponseEntity.ok(publicacionService.obtenerPorVendedor(vendedorId));
    }

    // GET /api/publicaciones/buscar?termino=calculo
    @GetMapping("/buscar")
    public ResponseEntity<List<Publicacion>> buscar(@RequestParam String termino) {
        return ResponseEntity.ok(publicacionService.buscar(termino));
    }

    // DELETE /api/publicaciones/1?vendedorId=1
    @DeleteMapping("/{id}")
    public ResponseEntity<?> desactivar(
            @PathVariable Long id,
            @RequestParam Long vendedorId) {
        try {
            publicacionService.desactivar(id, vendedorId);
            return ResponseEntity.ok("Publicación desactivada correctamente");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}