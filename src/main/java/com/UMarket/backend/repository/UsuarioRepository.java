package com.UMarket.backend.repository;

import com.UMarket.backend.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // Buscar usuario por email — lo usaremos para login y verificación .edu
    Optional<Usuario> findByEmail(String email);

    // Verificar si ya existe un email registrado — para evitar duplicados
    boolean existsByEmail(String email);

    // Buscar usuario por token de verificación — para confirmar el correo
    Optional<Usuario> findByTokenVerificacion(String token);

    // Solo usuarios activos — para el listado público
    List<Usuario> findAllByActivoTrue();
}