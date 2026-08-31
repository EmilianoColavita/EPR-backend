package com.epr.backend.repository;

import com.epr.backend.entity.Evaluacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EvaluacionRepository extends JpaRepository<Evaluacion, Long> {

    List<Evaluacion> findByAlumnoId(Long alumnoId);

    List<Evaluacion> findByAlumnoEmail(String email);

    Optional<Evaluacion> findByIdAndAlumnoId(Long id, Long alumnoId);
}
