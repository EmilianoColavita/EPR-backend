package com.epr.backend.repository;

import com.epr.backend.entity.AsignacionRutina;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AsignacionRutinaRepository extends JpaRepository<AsignacionRutina, Long> {
    Optional<AsignacionRutina> findByAlumnoIdAndActivaTrue(Long alumnoId);

    List<AsignacionRutina> findByRutinaIdAndActivaTrue(Long rutinaId);

    long countByRutinaIdAndActivaTrue(Long rutinaId);
}
