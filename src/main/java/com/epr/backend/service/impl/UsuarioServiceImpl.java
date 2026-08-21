package com.epr.backend.service.impl;

import com.epr.backend.dto.request.UsuarioCreateRequest;
import com.epr.backend.dto.request.UsuarioUpdateRequest;
import com.epr.backend.dto.response.UsuarioResponse;
import com.epr.backend.entity.Rol;
import com.epr.backend.entity.Usuario;
import com.epr.backend.exception.BadRequestException;
import com.epr.backend.exception.ResourceNotFoundException;
import com.epr.backend.mapper.UsuarioMapper;
import com.epr.backend.repository.UsuarioRepository;
import com.epr.backend.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public List<UsuarioResponse> listar(Rol rol) {
        List<Usuario> usuarios = rol == null ? usuarioRepository.findAll() : usuarioRepository.findByRol(rol);
        return usuarios.stream()
                .map(UsuarioMapper::toResponse)
                .toList();
    }

    @Override
    public UsuarioResponse obtenerPorId(Long id) {
        return UsuarioMapper.toResponse(buscarPorId(id));
    }

    @Override
    public UsuarioResponse obtenerPorEmail(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        return UsuarioMapper.toResponse(usuario);
    }

    @Override
    public UsuarioResponse crear(UsuarioCreateRequest request) {
        if (usuarioRepository.existsByEmail(request.email())) {
            throw new BadRequestException("Ya existe un usuario con ese email");
        }

        Usuario usuario = Usuario.builder()
                .nombre(request.nombre())
                .apellido(request.apellido())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .telefono(request.telefono())
                .rol(request.rol())
                .activo(true)
                .build();

        return UsuarioMapper.toResponse(usuarioRepository.save(usuario));
    }

    @Override
    public UsuarioResponse actualizar(Long id, UsuarioUpdateRequest request) {
        Usuario usuario = buscarPorId(id);

        if (!usuario.getEmail().equals(request.email()) && usuarioRepository.existsByEmail(request.email())) {
            throw new BadRequestException("Ya existe un usuario con ese email");
        }

        usuario.setNombre(request.nombre());
        usuario.setApellido(request.apellido());
        usuario.setEmail(request.email());
        usuario.setTelefono(request.telefono());
        usuario.setRol(request.rol());
        usuario.setActivo(request.activo());

        return UsuarioMapper.toResponse(usuarioRepository.save(usuario));
    }

    @Override
    public UsuarioResponse actualizarActivo(Long id, boolean activo) {
        Usuario usuario = buscarPorId(id);
        usuario.setActivo(activo);
        return UsuarioMapper.toResponse(usuarioRepository.save(usuario));
    }

    @Override
    public void eliminar(Long id) {
        Usuario usuario = buscarPorId(id);
        usuario.setActivo(false);
        usuarioRepository.save(usuario);
    }

    private Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
    }
}
