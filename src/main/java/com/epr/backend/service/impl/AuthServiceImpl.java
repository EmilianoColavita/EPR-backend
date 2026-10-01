package com.epr.backend.service.impl;

import com.epr.backend.dto.request.LoginRequest;
import com.epr.backend.dto.request.RegisterRequest;
import com.epr.backend.dto.response.LoginResponse;
import com.epr.backend.dto.response.UsuarioResponse;
import com.epr.backend.entity.Rol;
import com.epr.backend.entity.Usuario;
import com.epr.backend.exception.BadRequestException;
import com.epr.backend.mapper.UsuarioMapper;
import com.epr.backend.repository.UsuarioRepository;
import com.epr.backend.security.JwtService;
import com.epr.backend.security.RateLimiter;
import com.epr.backend.service.AuthService;
import com.epr.backend.service.NotificacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final NotificacionService notificacionService;
    private final RateLimiter rateLimiter;

    @Value("${epr.rate-limit.login.window-minutes}")
    private long loginVentanaMinutos;

    @Value("${epr.rate-limit.login.max-per-ip}")
    private int loginMaxPorIp;

    @Value("${epr.rate-limit.login.max-per-email}")
    private int loginMaxPorEmail;

    @Value("${epr.rate-limit.register.window-minutes}")
    private long registerVentanaMinutos;

    @Value("${epr.rate-limit.register.max-per-ip}")
    private int registerMaxPorIp;

    @Override
    public UsuarioResponse register(RegisterRequest request, String ipCliente) {
        rateLimiter.exigir("register:ip:" + ipCliente, registerMaxPorIp, Duration.ofMinutes(registerVentanaMinutos));

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
                .activo(false)
                .build();

        usuarioRepository.save(usuario);

        String mensaje = usuario.getNombre() + " " + usuario.getApellido() + " se registró y espera aprobación.";
        usuarioRepository.findByRol(Rol.ADMIN).forEach(admin ->
                notificacionService.crear(admin, "Nuevo alumno registrado", mensaje, "/panel/admin/alumnos"));

        return UsuarioMapper.toResponse(usuario);
    }

    @Override
    public LoginResponse login(LoginRequest request, String ipCliente) {
        // Por IP cuenta todos los intentos; por email frena la fuerza bruta sobre una cuenta
        // puntual y se reinicia con un login exitoso.
        Duration ventana = Duration.ofMinutes(loginVentanaMinutos);
        String claveEmail = "login:email:" + request.email().trim().toLowerCase(Locale.ROOT);
        rateLimiter.exigir("login:ip:" + ipCliente, loginMaxPorIp, ventana);
        rateLimiter.exigir(claveEmail, loginMaxPorEmail, ventana);

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password()));
        rateLimiter.reiniciar(claveEmail);

        Usuario usuario = usuarioRepository.findByEmail(request.email())
                .orElseThrow(() -> new BadRequestException("Credenciales inválidas"));

        String token = jwtService.generateToken(userDetailsService.loadUserByUsername(usuario.getEmail()));
        return new LoginResponse(token, UsuarioMapper.toResponse(usuario));
    }
}
