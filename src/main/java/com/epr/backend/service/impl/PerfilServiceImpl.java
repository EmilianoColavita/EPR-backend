package com.epr.backend.service.impl;

import com.epr.backend.dto.request.PerfilUpdateRequest;
import com.epr.backend.dto.response.FotoPerfilArchivoResponse;
import com.epr.backend.dto.response.UsuarioResponse;
import com.epr.backend.entity.FotoPerfil;
import com.epr.backend.entity.Usuario;
import com.epr.backend.exception.BadRequestException;
import com.epr.backend.exception.ResourceNotFoundException;
import com.epr.backend.mapper.UsuarioMapper;
import com.epr.backend.repository.FotoPerfilRepository;
import com.epr.backend.repository.UsuarioRepository;
import com.epr.backend.service.PerfilService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class PerfilServiceImpl implements PerfilService {

    private static final long TAMANIO_MAXIMO_FOTO_BYTES = 3L * 1024 * 1024;
    private static final Set<String> CONTENT_TYPES_PERMITIDOS = Set.of(
            "image/jpeg", "image/png", "image/webp"
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
        byte[] contenido = validarYLeer(archivo);

        FotoPerfil foto = fotoPerfilRepository.findByUsuarioEmail(email)
                .orElseGet(() -> FotoPerfil.builder().usuario(usuario).build());
        foto.setContentType(detectarContentType(contenido));
        foto.setArchivo(contenido);
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

    private byte[] validarYLeer(MultipartFile archivo) {
        if (archivo == null || archivo.isEmpty()) {
            throw new BadRequestException("El archivo es requerido");
        }
        String contentType = archivo.getContentType();
        if (contentType == null || !CONTENT_TYPES_PERMITIDOS.contains(contentType.toLowerCase())) {
            throw new BadRequestException(FOTO_INVALIDA);
        }
        if (archivo.getSize() > TAMANIO_MAXIMO_FOTO_BYTES) {
            throw new BadRequestException("La foto no puede superar los 3 MB");
        }

        byte[] contenido;
        try {
            contenido = archivo.getBytes();
        } catch (IOException e) {
            throw new BadRequestException("No se pudo leer el archivo");
        }

        // El content-type lo declara el cliente; se verifica también la firma real del archivo.
        if (detectarContentType(contenido) == null) {
            throw new BadRequestException(FOTO_INVALIDA);
        }
        return contenido;
    }

    private static String detectarContentType(byte[] b) {
        if (b.length >= 3 && (b[0] & 0xFF) == 0xFF && (b[1] & 0xFF) == 0xD8 && (b[2] & 0xFF) == 0xFF) {
            return "image/jpeg";
        }
        if (b.length >= 8 && (b[0] & 0xFF) == 0x89 && b[1] == 'P' && b[2] == 'N' && b[3] == 'G'
                && b[4] == 0x0D && b[5] == 0x0A && b[6] == 0x1A && b[7] == 0x0A) {
            return "image/png";
        }
        if (b.length >= 12 && b[0] == 'R' && b[1] == 'I' && b[2] == 'F' && b[3] == 'F'
                && b[8] == 'W' && b[9] == 'E' && b[10] == 'B' && b[11] == 'P') {
            return "image/webp";
        }
        return null;
    }

    private Usuario buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
    }
}
