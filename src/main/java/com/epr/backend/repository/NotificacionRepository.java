package com.epr.backend.repository;

import com.epr.backend.entity.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {

    List<Notificacion> findByDestinatarioEmailOrderByFechaDesc(String email);

    Optional<Notificacion> findByIdAndDestinatarioEmail(Long id, String email);

    List<Notificacion> findByDestinatarioEmailAndLeidaFalse(String email);
}
