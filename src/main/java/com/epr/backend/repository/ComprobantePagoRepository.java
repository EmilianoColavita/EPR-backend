package com.epr.backend.repository;

import com.epr.backend.dto.projection.ComprobantePagoResumen;
import com.epr.backend.entity.ComprobantePago;
import com.epr.backend.entity.EstadoComprobante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ComprobantePagoRepository extends JpaRepository<ComprobantePago, Long> {

    String SELECT_RESUMEN = """
            SELECT new com.epr.backend.dto.projection.ComprobantePagoResumen(
                c.id, a, c.nombreArchivo, c.contentType, c.fecha, pc, c.monto, c.estado, p, c.notaRechazo, c.fechaSubida)
            FROM ComprobantePago c
            JOIN c.alumno a
            LEFT JOIN c.planCuota pc
            LEFT JOIN c.pago p
            """;

    @Query(SELECT_RESUMEN + "WHERE a.id = :alumnoId ORDER BY c.fechaSubida DESC")
    List<ComprobantePagoResumen> listarResumenPorAlumnoId(@Param("alumnoId") Long alumnoId);

    @Query(SELECT_RESUMEN + "WHERE a.email = :email ORDER BY c.fechaSubida DESC")
    List<ComprobantePagoResumen> listarResumenPorAlumnoEmail(@Param("email") String email);

    @Query(SELECT_RESUMEN + "WHERE c.estado = :estado ORDER BY c.fechaSubida DESC")
    List<ComprobantePagoResumen> listarResumenPorEstado(@Param("estado") EstadoComprobante estado);

    @Query(SELECT_RESUMEN + "ORDER BY c.fechaSubida DESC")
    List<ComprobantePagoResumen> listarResumen();

    Optional<ComprobantePago> findByIdAndAlumnoId(Long id, Long alumnoId);
}
