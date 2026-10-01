package com.epr.backend.service.impl;

import com.epr.backend.dto.request.PerfilUpdateRequest;
import com.epr.backend.dto.response.FotoPerfilArchivoResponse;
import com.epr.backend.dto.response.UsuarioResponse;
import com.epr.backend.entity.FotoPerfil;
import com.epr.backend.entity.Usuario;
import com.epr.backend.exception.ResourceNotFoundException;
import com.epr.backend.mapper.UsuarioMapper;
import com.epr.backend.repository.FotoPerfilRepository;
import com.epr.backend.repository.UsuarioRepository;
import com.epr.backend.service.PerfilService;
import com.epr.backend.util.ArchivoSubido;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class PerfilServiceImpl implements PerfilService {

    private static final long TAMANIO_MAXIMO_FOTO_BYTES = 3L * 1024 * 1024;
    private static final Set<String> CONTENT_TYPES_PERMITIDOS = Set.of(
            ArchivoSubido.JPEG, ArchivoSubido.PNG, ArchivoSubido.WEBP
    );
    private static final String FOTO_INVALIDA = "La foto debe ser una imagen JPG, PNG o WEBP";

    private final UsuarioRepository usuarioRepository;
    private final FotoPerfilRepository fotoPerfilRepository;

    @Override
    @Transactional
    public UsuarioResponse actualizar(String email, PerfilUpdateRequest request) {
        Usuario usuario = buscarPorEmail(email);

        usuario.setNombre(request.nombre());
        usuario.setApellido(request.apellido());
        usuario.setTelefono(request.telefono());

        return UsuarioMapper.toResponse(usuarioRepository.save(usuario));
    }

    @Override
    @Transactional
    public UsuarioResponse subirFoto(String email, MultipartFile archivo) {
        Usuario usuario = buscarPorEmail(email);
        ArchivoSubido.Contenido contenido = ArchivoSubido.leerYValidar(
                archivo, CONTENT_TYPES_PERMITIDOS, TAMANIO_MAXIMO_FOTO_BYTES, FOTO_INVALIDA);

        FotoPerfil foto = fotoPerfilRepository.findByUsuarioEmail(email)
                .orElseGet(() -> FotoPerfil.builder().usuario(usuario).build());
        foto.setContentType(contenido.contentType());
        foto.setArchivo(contenido.bytes());
        fotoPerfilRepository.save(foto);

        return UsuarioMapper.toResponse(usuario);
    }

    @Override
    public FotoPerfilArchivoResponse obtenerFoto(String email) {
        FotoPerfil foto = fotoPerfilRepository.findByUsuarioEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("No tenés foto de perfil"));
        return new FotoPerfilArchivoResponse(foto.getContentType(), foto.getArchivo());
    }

    @Override
    @Transactional
    public void eliminarFoto(String email) {
        fotoPerfilRepository.eliminarPorUsuarioEmail(email);
    }

    private Usuario buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
    }
}
