package com.epr.backend.service;

import com.epr.backend.dto.request.PerfilUpdateRequest;
import com.epr.backend.dto.response.FotoPerfilArchivoResponse;
import com.epr.backend.dto.response.UsuarioResponse;
import org.springframework.web.multipart.MultipartFile;

public interface PerfilService {
    UsuarioResponse actualizar(String email, PerfilUpdateRequest request);

    UsuarioResponse subirFoto(String email, MultipartFile archivo);

    FotoPerfilArchivoResponse obtenerFoto(String email);

    void eliminarFoto(String email);
}
