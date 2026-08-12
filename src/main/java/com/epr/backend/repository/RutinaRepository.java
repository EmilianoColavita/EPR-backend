package com.epr.backend.repository;

import com.epr.backend.entity.Rutina;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RutinaRepository extends JpaRepository<Rutina, Long> {
    List<Rutina> findByAlumnoId(Long alumnoId);
}
