package com.epr.backend.service.impl;

import com.epr.backend.dto.response.EstadoCuentaResponse;
import com.epr.backend.entity.Cuota;
import com.epr.backend.repository.CuotaRepository;
import com.epr.backend.service.CuotaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CuotaServiceImpl implements CuotaService {

    private final CuotaRepository cuotaRepository;

    @Override
    public EstadoCuentaResponse obtenerMiEstado(String email) {
        return cuotaRepository.findByAlumnoEmail(email)
                .map(this::toResponse)
                .orElseGet(EstadoCuentaResponse::sinCuota);
    }

    private EstadoCuentaResponse toResponse(Cuota cuota) {
        return new EstadoCuentaResponse(cuota.isAlDia(), cuota.getProximoVencimiento());
    }
}
