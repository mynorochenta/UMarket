package com.UMarket.backend.repository;

import com.UMarket.backend.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    // Buscar categoría por nombre — útil para no duplicar categorías
    Optional<Categoria> findByNombre(String nombre);
}