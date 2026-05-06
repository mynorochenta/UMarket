package com.UMarket.backend.repository;

import com.UMarket.backend.model.Resena;
import com.UMarket.backend.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResenaRepository extends JpaRepository<Resena, Long> {

    // Todas las reseñas que recibió un vendedor
    List<Resena> findByVendedor(Usuario vendedor);

    // Calcular el promedio de calificación de un vendedor
    // Esto es el "rating" que verán en el perfil — clave para la confianza
    @Query("SELECT AVG(r.calificacion) FROM Resena r WHERE r.vendedor = :vendedor")
    Double calcularPromedioCalificacion(@Param("vendedor") Usuario vendedor);
}