package com.epr.backend.security;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

/**
 * UserDetails con la versión de token del usuario, para poder invalidar los JWT emitidos
 * antes de un cambio de contraseña.
 */
@Getter
public class UsuarioPrincipal extends User {

    private final int tokenVersion;

    public UsuarioPrincipal(String email, String password, boolean activo, int tokenVersion,
                            Collection<? extends GrantedAuthority> authorities) {
        super(email, password, activo, true, true, true, authorities);
        this.tokenVersion = tokenVersion;
    }
}
