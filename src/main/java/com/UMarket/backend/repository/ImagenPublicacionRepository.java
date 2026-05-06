package com.UMarket.backend.repository;

import com.UMarket.backend.model.ImagenPublicacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImagenPublicacionRepository extends JpaRepository<ImagenPublicacion, Long> {

    // Obtener todas las imágenes de una publicación en orden
    List<ImagenPublicacion> findByPublicacionIdOrderByOrdenAsc(Long publicacionId);
}