package com.UMarket.backend.repository;

import com.UMarket.backend.model.Mensaje;
import com.UMarket.backend.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MensajeRepository extends JpaRepository<Mensaje, Long> {

    // Todos los mensajes recibidos por un usuario (su bandeja de entrada)
    List<Mensaje> findByDestinatarioOrderByFechaDesc(Usuario destinatario);

    // Todos los mensajes enviados por un usuario
    List<Mensaje> findByRemitenteOrderByFechaDesc(Usuario remitente);

    // Contar mensajes no leídos — para mostrar la notificación en el navbar
    long countByDestinatarioAndLeidoFalse(Usuario destinatario);
}