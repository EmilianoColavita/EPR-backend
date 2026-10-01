package com.epr.backend.dto.projection;

import java.time.LocalDate;

/**
 * Datos de un archivo de alumno (evaluación o rutina en PDF) sin el contenido. Los listados se
 * consultan con esta proyección para no traer el BLOB de cada fila a memoria.
 */
public record ArchivoAlumnoResumen(
        Long id,
        Long alumnoId,
        String alumnoNombre,
        String alumnoApellido,
        String nombreArchivo,
        LocalDate fechaSubida
) {
}
