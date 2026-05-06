package com.UMarket.backend.controller;

import com.UMarket.backend.model.Mensaje;
import com.UMarket.backend.service.MensajeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mensajes")
@CrossOrigin(origins = "*")
public class MensajeController {

    @Autowired
    private MensajeService mensajeService;

    // POST /api/mensajes?remitenteId=1&destinatarioId=2
    @PostMapping
    public ResponseEntity<?> enviar(
            @RequestBody Mensaje mensaje,
            @RequestParam Long remitenteId,
            @RequestParam Long destinatarioId) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(mensajeService.enviar(mensaje, remitenteId, destinatarioId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // GET /api/mensajes/bandeja/1
    @GetMapping("/bandeja/{usuarioId}")
    public ResponseEntity<List<Mensaje>> obtenerBandeja(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(mensajeService.obtenerBandeja(usuarioId));
    }

    // GET /api/mensajes/noleidos/1
    @GetMapping("/noleidos/{usuarioId}")
    public ResponseEntity<Long> contarNoLeidos(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(mensajeService.contarNoLeidos(usuarioId));
    }

    // PUT /api/mensajes/1/leido
    @PutMapping("/{id}/leido")
    public ResponseEntity<?> marcarLeido(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(mensajeService.marcarLeido(id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}