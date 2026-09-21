package com.epr.backend.controller;

import com.epr.backend.dto.request.PerfilUpdateRequest;
import com.epr.backend.dto.response.FotoPerfilArchivoResponse;
import com.epr.backend.dto.response.UsuarioResponse;
import com.epr.backend.service.PerfilService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/usuarios/mi-perfil")
@RequiredArgsConstructor
public class MiPerfilController {

    private final PerfilService perfilService;

    @PutMapping
    public ResponseEntity<UsuarioResponse> actualizar(@Valid @RequestBody PerfilUpdateRequest request,
                                                      Authentication authentication) {
        return ResponseEntity.ok(perfilService.actualizar(authentication.getName(), request));
    }

    @PostMapping(value = "/foto", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UsuarioResponse> subirFoto(
            @RequestParam(value = "archivo", required = false) MultipartFile archivo,
            Authentication authentication) {
        return ResponseEntity.ok(perfilService.subirFoto(authentication.getName(), archivo));
    }

    @GetMapping("/foto")
    public ResponseEntity<byte[]> obtenerFoto(Authentication authentication) {
        FotoPerfilArchivoResponse foto = perfilService.obtenerFoto(authentication.getName());
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(foto.contentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.inline().build().toString())
                .body(foto.contenido());
    }

    @DeleteMapping("/foto")
    public ResponseEntity<Void> eliminarFoto(Authentication authentication) {
        perfilService.eliminarFoto(authentication.getName());
        return ResponseEntity.noContent().build();
    }
}
