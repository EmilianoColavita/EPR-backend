package com.epr.backend.repository;

import com.epr.backend.dto.projection.ArchivoAlumnoResumen;
import com.epr.backend.entity.RutinaPdf;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RutinaPdfRepository extends JpaRepository<RutinaPdf, Long> {

    @Query("""
            SELECT new com.epr.backend.dto.projection.ArchivoAlumnoResumen(
                r.id, a.id, a.nombre, a.apellido, r.nombreArchivo, r.fechaSubida)
            FROM RutinaPdf r JOIN r.alumno a
            WHERE a.id = :alumnoId
            ORDER BY r.id
            """)
    List<ArchivoAlumnoResumen> listarResumenPorAlumnoId(@Param("alumnoId") Long alumnoId);

    @Query("""
            SELECT new com.epr.backend.dto.projection.ArchivoAlumnoResumen(
                r.id, a.id, a.nombre, a.apellido, r.nombreArchivo, r.fechaSubida)
            FROM RutinaPdf r JOIN r.alumno a
            WHERE a.email = :email
            ORDER BY r.id
            """)
    List<ArchivoAlumnoResumen> listarResumenPorAlumnoEmail(@Param("email") String email);

    Optional<RutinaPdf> findByIdAndAlumnoId(Long id, Long alumnoId);
}
