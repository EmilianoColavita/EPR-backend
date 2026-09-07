package com.epr.backend.mapper;

import com.epr.backend.dto.response.AlumnoResumenResponse;
import com.epr.backend.dto.response.ComprobantePagoArchivoResponse;
import com.epr.backend.dto.response.ComprobantePagoResponse;
import com.epr.backend.entity.ComprobantePago;
import com.epr.backend.entity.Usuario;

public class ComprobantePagoMapper {

    private ComprobantePagoMapper() {
    }

    public static ComprobantePagoResponse toResponse(ComprobantePago comprobante) {
        Usuario alumno = comprobante.getAlumno();
        return new ComprobantePagoResponse(
                comprobante.getId(),
                new AlumnoResumenResponse(alumno.getId(), alumno.getNombre(), alumno.getApellido()),
                comprobante.getNombreArchivo(),
                comprobante.getContentType(),
                comprobante.getFecha(),
                comprobante.getPlanCuota() != null ? PlanCuotaMapper.toResponse(comprobante.getPlanCuota()) : null,
                comprobante.getMonto(),
                comprobante.getEstado(),
                comprobante.getPago() != null ? PagoMapper.toResponse(comprobante.getPago()) : null,
                comprobante.getNotaRechazo(),
                comprobante.getFechaSubida()
        );
    }

    public static ComprobantePagoArchivoResponse toArchivoResponse(ComprobantePago comprobante) {
        return new ComprobantePagoArchivoResponse(
                comprobante.getNombreArchivo(),
                comprobante.getContentType(),
                comprobante.getArchivo()
        );
    }
}
