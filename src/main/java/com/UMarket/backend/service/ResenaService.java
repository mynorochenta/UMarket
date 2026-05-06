package com.UMarket.backend.service;

import com.UMarket.backend.model.Resena;
import com.UMarket.backend.model.Usuario;
import com.UMarket.backend.repository.ResenaRepository;
import com.UMarket.backend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ResenaService {

    @Autowired
    private ResenaRepository resenaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    public Resena crear(Resena resena, Long autorId, Long vendedorId) {

        // No podés reseñarte a vos mismo
        if (autorId.equals(vendedorId)) {
            throw new RuntimeException("No podés reseñarte a vos mismo");
        }

        // REGLA DE NEGOCIO: Calificación debe ser entre 1 y 5
        if (resena.getCalificacion() < 1 || resena.getCalificacion() > 5) {
            throw new RuntimeException("La calificación debe ser entre 1 y 5");
        }

        Usuario autor = usuarioRepository.findById(autorId)
                .orElseThrow(() -> new RuntimeException("Autor no encontrado"));

        Usuario vendedor = usuarioRepository.findById(vendedorId)
                .orElseThrow(() -> new RuntimeException("Vendedor no encontrado"));

        resena.setAutor(autor);
        resena.setVendedor(vendedor);
        resena.setFecha(LocalDateTime.now());

        return resenaRepository.save(resena);
    }

    public List<Resena> obtenerPorVendedor(Long vendedorId) {
        Usuario vendedor = usuarioRepository.findById(vendedorId)
                .orElseThrow(() -> new RuntimeException("Vendedor no encontrado"));
        return resenaRepository.findByVendedor(vendedor);
    }

    public Double obtenerPromedio(Long vendedorId) {
        Usuario vendedor = usuarioRepository.findById(vendedorId)
                .orElseThrow(() -> new RuntimeException("Vendedor no encontrado"));
        Double promedio = resenaRepository.calcularPromedioCalificacion(vendedor);
        // Si no tiene reseñas aún, retornamos 0.0
        return promedio != null ? promedio : 0.0;
    }
}