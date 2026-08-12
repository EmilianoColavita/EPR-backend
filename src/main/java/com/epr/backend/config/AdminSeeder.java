package com.epr.backend.config;

import com.epr.backend.entity.Rol;
import com.epr.backend.entity.Usuario;
import com.epr.backend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminSeeder implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${epr.admin.email}")
    private String adminEmail;

    @Value("${epr.admin.password}")
    private String adminPassword;

    @Override
    public void run(String... args) {
        if (usuarioRepository.count() == 0) {
            Usuario admin = Usuario.builder()
                    .nombre("Admin")
                    .apellido("EPR")
                    .email(adminEmail)
                    .password(passwordEncoder.encode(adminPassword))
                    .rol(Rol.ADMIN)
                    .activo(true)
                    .build();
            usuarioRepository.save(admin);
        }
    }
}
