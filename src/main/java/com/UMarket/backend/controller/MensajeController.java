package com.UMarket.backend.controller;

import com.UMarket.backend.model.Mensaje;
import com.UMarket.backend.service.MensajeService;
import com.UMarket.backend.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mensajes")
public class MensajeController {

    @Autowired
    private MensajeService mensajeService;

    @Autowired
    private UsuarioService usuarioService;

    // POST /api/mensajes?destinatarioId=2&publicacionId=5
    @PostMapping
    public ResponseEntity<?> enviar(
            @RequestBody Mensaje mensaje,
            @RequestParam Long destinatarioId,
            @RequestParam Long publicacionId) {
        try {
            Long remitenteId = getIdAutenticado();
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(mensajeService.enviar(mensaje, remitenteId, destinatarioId, publicacionId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // GET /api/mensajes/bandeja — bandeja del usuario autenticado
    @GetMapping("/bandeja")
    public ResponseEntity<List<Mensaje>> obtenerBandeja() {
        Long usuarioId = getIdAutenticado();
        return ResponseEntity.ok(mensajeService.obtenerBandeja(usuarioId));
    }

    // GET /api/mensajes/noleidos — conteo del usuario autenticado
    @GetMapping("/noleidos")
    public ResponseEntity<Long> contarNoLeidos() {
        Long usuarioId = getIdAutenticado();
        return ResponseEntity.ok(mensajeService.contarNoLeidos(usuarioId));
    }

    // PUT /api/mensajes/1/leido
    @PutMapping("/{id}/leido")
    public ResponseEntity<?> marcarLeido(@PathVariable Long id) {
        try {
            Long usuarioId = getIdAutenticado();
            return ResponseEntity.ok(mensajeService.marcarLeido(id, usuarioId));
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
