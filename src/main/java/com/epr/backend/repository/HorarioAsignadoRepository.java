package com.epr.backend.repository;

import com.epr.backend.entity.HorarioAsignado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface HorarioAsignadoRepository extends JpaRepository<HorarioAsignado, Long> {

    Optional<HorarioAsignado> findByAlumnoIdAndActivoTrue(Long alumnoId);

    @Query("SELECT h FROM HorarioAsignado h WHERE h.activo = true AND h.alumno.activo = true")
    List<HorarioAsignado> buscarActivosDeAlumnosActivos();
}
