package com.UMarket.backend.controller;

import com.UMarket.backend.model.Publicacion;
import com.UMarket.backend.service.PublicacionService;
import com.UMarket.backend.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/publicaciones")
public class PublicacionController {

    @Autowired
    private PublicacionService publicacionService;

    @Autowired
    private UsuarioService usuarioService;

    // POST /api/publicaciones
    @PostMapping
    public ResponseEntity<?> crear(@RequestBody Publicacion publicacion) {
        try {
            Long vendedorId = getIdAutenticado();
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

    // DELETE /api/publicaciones/1
    @DeleteMapping("/{id}")
    public ResponseEntity<?> desactivar(@PathVariable Long id) {
        try {
            Long vendedorId = getIdAutenticado();
            publicacionService.desactivar(id, vendedorId);
            return ResponseEntity.ok("Publicación desactivada correctamente");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    private Long getIdAutenticado() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return usuarioService.obtenerPorEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario autenticado no encontrado"))
                .getId();
    }
}
