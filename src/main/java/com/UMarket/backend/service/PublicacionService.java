package com.UMarket.backend.service;

import com.UMarket.backend.model.Publicacion;
import com.UMarket.backend.model.Usuario;
import com.UMarket.backend.repository.PublicacionRepository;
import com.UMarket.backend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PublicacionService {

    @Autowired
    private PublicacionRepository publicacionRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    // ─── CREAR PUBLICACIÓN ──────────────────────────────────────

    public Publicacion crear(Publicacion publicacion, Long vendedorId) {

        Usuario vendedor = usuarioRepository.findById(vendedorId)
                .orElseThrow(() -> new RuntimeException("Vendedor no encontrado"));

        if (!vendedor.isVerificado()) {
            throw new RuntimeException("Debes verificar tu correo antes de publicar");
        }

        if (publicacion.getPrecio() <= 0) {
            throw new RuntimeException("El precio debe ser mayor a Q0.00");
        }

        publicacion.setVendedor(vendedor);
        publicacion.setActivo(true);
        publicacion.setFechaPublicacion(LocalDateTime.now());

        return publicacionRepository.save(publicacion);
    }

    // ─── CONSULTAS ──────────────────────────────────────────────

    // El feed principal — lo que ven todos al entrar
    public List<Publicacion> obtenerFeed() {
        return publicacionRepository.findFeedPrincipal();
    }

    public Optional<Publicacion> obtenerPorId(Long id) {
        return publicacionRepository.findById(id).filter(Publicacion::isActivo);
    }

    public List<Publicacion> obtenerPorVendedor(Long vendedorId) {
        Usuario vendedor = usuarioRepository.findById(vendedorId)
                .orElseThrow(() -> new RuntimeException("Vendedor no encontrado"));
        return publicacionRepository.findByVendedorAndActivoTrue(vendedor);
    }

    public List<Publicacion> buscar(String termino) {
        return publicacionRepository.findByTituloContainingIgnoreCaseAndActivoTrue(termino);
    }

    // ─── DESACTIVAR (no borrar) ──────────────────────────────────

    public void desactivar(Long id, Long vendedorId) {
        Publicacion publicacion = publicacionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Publicación no encontrada"));

        // SEGURIDAD: Solo el dueño puede desactivar su publicación
        if (!publicacion.getVendedor().getId().equals(vendedorId)) {
            throw new RuntimeException("No tenés permiso para modificar esta publicación");
        }

        publicacion.setActivo(false);
        publicacionRepository.save(publicacion);
    }
}