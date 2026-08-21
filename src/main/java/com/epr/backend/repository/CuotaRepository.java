package com.epr.backend.repository;

import com.epr.backend.entity.Cuota;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CuotaRepository extends JpaRepository<Cuota, Long> {
    Optional<Cuota> findByAlumnoEmail(String email);
}
