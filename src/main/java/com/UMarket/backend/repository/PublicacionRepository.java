package com.UMarket.backend.repository;

import com.UMarket.backend.model.Publicacion;
import com.UMarket.backend.model.Usuario;
import com.UMarket.backend.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PublicacionRepository extends JpaRepository<Publicacion, Long> {

    // Todas las publicaciones activas de un vendedor
    List<Publicacion> findByVendedorAndActivoTrue(Usuario vendedor);

    // Todas las publicaciones activas de una categoría
    List<Publicacion> findByCategoriaAndActivoTrue(Categoria categoria);

    // Buscar por título (búsqueda parcial, no distingue mayúsculas)
    // Esto alimentará la barra de búsqueda del feed
    List<Publicacion> findByTituloContainingIgnoreCaseAndActivoTrue(String titulo);

    // Contar cuántas publicaciones activas tiene un vendedor
    // ← CLAVE para el modelo freemium: primeras 3 gratis
    long countByVendedorAndActivoTrue(Usuario vendedor);

    // Query personalizada — publicaciones activas ordenadas por fecha (más recientes primero)
    // Esto es el feed principal que verán los estudiantes
    @Query("SELECT p FROM Publicacion p WHERE p.activo = true ORDER BY p.fechaPublicacion DESC")
    List<Publicacion> findFeedPrincipal();
}