package com.epr.backend.repository;

import com.epr.backend.entity.Pago;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PagoRepository extends JpaRepository<Pago, Long> {
    List<Pago> findByAlumnoIdOrderByFechaDesc(Long alumnoId);
}
