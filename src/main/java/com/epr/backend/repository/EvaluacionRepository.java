package com.epr.backend.repository;

import com.epr.backend.dto.projection.ArchivoAlumnoResumen;
import com.epr.backend.entity.Evaluacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EvaluacionRepository extends JpaRepository<Evaluacion, Long> {

    @Query("""
            SELECT new com.epr.backend.dto.projection.ArchivoAlumnoResumen(
                e.id, a.id, a.nombre, a.apellido, e.nombreArchivo, e.fechaSubida)
            FROM Evaluacion e JOIN e.alumno a
            WHERE a.id = :alumnoId
            ORDER BY e.id
            """)
    List<ArchivoAlumnoResumen> listarResumenPorAlumnoId(@Param("alumnoId") Long alumnoId);

    @Query("""
            SELECT new com.epr.backend.dto.projection.ArchivoAlumnoResumen(
                e.id, a.id, a.nombre, a.apellido, e.nombreArchivo, e.fechaSubida)
            FROM Evaluacion e JOIN e.alumno a
            WHERE a.email = :email
            ORDER BY e.id
            """)
    List<ArchivoAlumnoResumen> listarResumenPorAlumnoEmail(@Param("email") String email);

    Optional<Evaluacion> findByIdAndAlumnoId(Long id, Long alumnoId);
}
