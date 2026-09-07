package com.epr.backend.repository;

import com.epr.backend.entity.ComprobantePago;
import com.epr.backend.entity.EstadoComprobante;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ComprobantePagoRepository extends JpaRepository<ComprobantePago, Long> {

    List<ComprobantePago> findByAlumnoIdOrderByFechaSubidaDesc(Long alumnoId);

    List<ComprobantePago> findByAlumnoEmailOrderByFechaSubidaDesc(String email);

    Optional<ComprobantePago> findByIdAndAlumnoId(Long id, Long alumnoId);

    List<ComprobantePago> findByEstadoOrderByFechaSubidaDesc(EstadoComprobante estado);

    List<ComprobantePago> findAllByOrderByFechaSubidaDesc();
}
