package com.epr.backend.repository;

import com.epr.backend.entity.Beca;
import com.epr.backend.entity.EstadoBeca;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BecaRepository extends JpaRepository<Beca, Long> {
    Optional<Beca> findByAlumnoIdAndEstado(Long alumnoId, EstadoBeca estado);

    Optional<Beca> findByAlumnoEmailAndEstado(String email, EstadoBeca estado);

    Optional<Beca> findFirstByAlumnoIdOrderByFechaInicioDescIdDesc(Long alumnoId);

    List<Beca> findByAlumnoIdInAndEstado(List<Long> alumnoIds, EstadoBeca estado);
}
