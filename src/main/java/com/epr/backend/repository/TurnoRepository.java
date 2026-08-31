package com.epr.backend.repository;

import com.epr.backend.entity.EstadoTurno;
import com.epr.backend.entity.Turno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface TurnoRepository extends JpaRepository<Turno, Long> {

    List<Turno> findByHorarioAsignadoIdAndFechaBetween(Long horarioAsignadoId, LocalDate desde, LocalDate hasta);

    List<Turno> findByHorarioAsignadoIdAndFechaGreaterThanEqualAndEstado(Long horarioAsignadoId, LocalDate desde, EstadoTurno estado);

    /**
     * Inserta el turno generado por una franja de horario solo si no existe ya uno con la misma
     * combinación (horario_asignado_id, fecha, hora_inicio) — la constraint única
     * uk_turnos_horario_fecha_hora descarta silenciosamente el INSERT si otra transacción
     * concurrente ya generó ese turno, evitando duplicados sin lanzar una excepción que
     * interrumpiría el resto de la transacción.
     */
    @Modifying
    @Query(value = """
            INSERT IGNORE INTO turnos
                (alumno_id, fecha, hora_inicio, hora_fin, actividad, estado, notas, creado_por_id, horario_asignado_id, fecha_creacion, fecha_actualizacion)
            VALUES
                (:alumnoId, :fecha, :horaInicio, :horaFin, :actividad, 'CONFIRMADO', :notas, :creadoPorId, :horarioAsignadoId, NOW(), NOW())
            """, nativeQuery = true)
    void insertarSiNoExiste(@Param("alumnoId") Long alumnoId,
                             @Param("fecha") LocalDate fecha,
                             @Param("horaInicio") LocalTime horaInicio,
                             @Param("horaFin") LocalTime horaFin,
                             @Param("actividad") String actividad,
                             @Param("notas") String notas,
                             @Param("creadoPorId") Long creadoPorId,
                             @Param("horarioAsignadoId") Long horarioAsignadoId);

    @Query("""
            SELECT t FROM Turno t
            WHERE (:alumnoId IS NULL OR t.alumno.id = :alumnoId)
            AND (:desde IS NULL OR t.fecha >= :desde)
            AND (:hasta IS NULL OR t.fecha <= :hasta)
            ORDER BY t.fecha ASC, t.horaInicio ASC
            """)
    List<Turno> buscar(@Param("alumnoId") Long alumnoId, @Param("desde") LocalDate desde, @Param("hasta") LocalDate hasta);

    @Query("""
            SELECT t FROM Turno t
            WHERE t.alumno.email = :email
            AND (:desde IS NULL OR t.fecha >= :desde)
            AND (:hasta IS NULL OR t.fecha <= :hasta)
            ORDER BY t.fecha ASC, t.horaInicio ASC
            """)
    List<Turno> buscarPorAlumnoEmail(@Param("email") String email, @Param("desde") LocalDate desde, @Param("hasta") LocalDate hasta);
}
