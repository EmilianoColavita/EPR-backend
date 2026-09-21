package com.epr.backend.repository;

import com.epr.backend.entity.FotoPerfil;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface FotoPerfilRepository extends JpaRepository<FotoPerfil, Long> {

    Optional<FotoPerfil> findByUsuarioEmail(String email);

    @Modifying
    @Query("delete from FotoPerfil f where f.usuario.email = :email")
    void eliminarPorUsuarioEmail(@Param("email") String email);
}
