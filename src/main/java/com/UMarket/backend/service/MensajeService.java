package com.UMarket.backend.service;

import com.UMarket.backend.model.Mensaje;
import com.UMarket.backend.model.Usuario;
import com.UMarket.backend.repository.MensajeRepository;
import com.UMarket.backend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MensajeService {

    @Autowired
    private MensajeRepository mensajeRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    public Mensaje enviar(Mensaje mensaje, Long remitenteId, Long destinatarioId) {

        // No podés mandarte mensajes a vos mismo
        if (remitenteId.equals(destinatarioId)) {
            throw new RuntimeException("No podés enviarte mensajes a vos mismo");
        }

        Usuario remitente = usuarioRepository.findById(remitenteId)
                .orElseThrow(() -> new RuntimeException("Remitente no encontrado"));

        Usuario destinatario = usuarioRepository.findById(destinatarioId)
                .orElseThrow(() -> new RuntimeException("Destinatario no encontrado"));

        mensaje.setRemitente(remitente);
        mensaje.setDestinatario(destinatario);
        mensaje.setFecha(LocalDateTime.now());
        mensaje.setLeido(false);

        return mensajeRepository.save(mensaje);
    }

    public List<Mensaje> obtenerBandeja(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return mensajeRepository.findByDestinatarioOrderByFechaDesc(usuario);
    }

    public long contarNoLeidos(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return mensajeRepository.countByDestinatarioAndLeidoFalse(usuario);
    }

    public Mensaje marcarLeido(Long mensajeId) {
        Mensaje mensaje = mensajeRepository.findById(mensajeId)
                .orElseThrow(() -> new RuntimeException("Mensaje no encontrado"));
        mensaje.setLeido(true);
        return mensajeRepository.save(mensaje);
    }
}