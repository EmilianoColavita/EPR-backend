package com.epr.backend.repository;

import com.epr.backend.entity.Cuota;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CuotaRepository extends JpaRepository<Cuota, Long> {
    Optional<Cuota> findByAlumnoEmail(String email);

    Optional<Cuota> findByAlumnoId(Long alumnoId);

    List<Cuota> findByAlumnoIdIn(List<Long> alumnoIds);
}
