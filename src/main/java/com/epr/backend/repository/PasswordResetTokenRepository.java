package com.epr.backend.repository;

import com.epr.backend.entity.PasswordResetToken;
import com.epr.backend.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    Optional<PasswordResetToken> findByToken(String token);

    /**
     * Consume el token de forma atómica: solo tiene efecto si existe, no fue usado y no venció.
     * Devuelve la cantidad de filas afectadas (1 si se consumió, 0 si no era válido), lo que evita
     * que dos requests simultáneos usen el mismo token.
     */
    @Modifying
    @Query("update PasswordResetToken t set t.usado = true "
            + "where t.token = :token and t.usado = false and t.fechaExpiracion > :ahora")
    int consumirSiVigente(@Param("token") String token, @Param("ahora") LocalDateTime ahora);

    @Modifying
    @Query("update PasswordResetToken t set t.usado = true where t.usuario = :usuario and t.usado = false")
    void invalidarPendientes(@Param("usuario") Usuario usuario);
}
