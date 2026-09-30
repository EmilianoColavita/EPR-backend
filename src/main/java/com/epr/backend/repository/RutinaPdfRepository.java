package com.epr.backend.repository;

import com.epr.backend.entity.RutinaPdf;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RutinaPdfRepository extends JpaRepository<RutinaPdf, Long> {

    List<RutinaPdf> findByAlumnoId(Long alumnoId);

    List<RutinaPdf> findByAlumnoEmail(String email);

    Optional<RutinaPdf> findByIdAndAlumnoId(Long id, Long alumnoId);
}
