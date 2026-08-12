package com.epr.backend.service.impl;

import com.epr.backend.dto.request.LoginRequest;
import com.epr.backend.dto.request.RegisterRequest;
import com.epr.backend.dto.response.LoginResponse;
import com.epr.backend.entity.Rol;
import com.epr.backend.entity.Usuario;
import com.epr.backend.exception.BadRequestException;
import com.epr.backend.mapper.UsuarioMapper;
import com.epr.backend.repository.UsuarioRepository;
import com.epr.backend.security.JwtService;
import com.epr.backend.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;

    @Override
    public LoginResponse register(RegisterRequest request) {
        if (usuarioRepository.existsByEmail(request.email())) {
            throw new BadRequestException("Ya existe un usuario con ese email");
        }

        Usuario usuario = Usuario.builder()
                .nombre(request.nombre())
                .apellido(request.apellido())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .telefono(request.telefono())
                .rol(Rol.ALUMNO)
                .activo(true)
                .build();

        usuarioRepository.save(usuario);

        String token = jwtService.generateToken(userDetailsService.loadUserByUsername(usuario.getEmail()));
        return new LoginResponse(token, UsuarioMapper.toResponse(usuario));
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password()));

        Usuario usuario = usuarioRepository.findByEmail(request.email())
                .orElseThrow(() -> new BadRequestException("Credenciales inválidas"));

        String token = jwtService.generateToken(userDetailsService.loadUserByUsername(usuario.getEmail()));
        return new LoginResponse(token, UsuarioMapper.toResponse(usuario));
    }
}
