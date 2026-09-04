package com.epr.backend.service;

import com.epr.backend.dto.request.PagoRequest;
import com.epr.backend.dto.response.CuentaAlumnoResponse;
import com.epr.backend.dto.response.EstadoCuentaResponse;
import com.epr.backend.dto.response.PagoResponse;
import com.epr.backend.dto.response.ResumenCuotasResponse;

import java.util.List;

public interface CuotaService {

    EstadoCuentaResponse obtenerMiEstado(String email);

    CuentaAlumnoResponse obtenerCuenta(Long alumnoId);

    List<PagoResponse> listarPagos(Long alumnoId);

    PagoResponse registrarPago(Long alumnoId, PagoRequest request);

    ResumenCuotasResponse obtenerResumen();
}
