package com.fran.saludconecta.seguridad;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import com.fran.saludconecta.usuario.dto.UsuarioDTO;
import java.util.Collection;
import java.util.Collections;

public class UsuarioDetalles implements UserDetails {

    private final UsuarioDTO usuario;

    public UsuarioDetalles(UsuarioDTO usuario) {
        this.usuario = usuario;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Convertimos el rol del usuario a GrantedAuthority
        // (Spring Security entiende esto para autorización)
        if (usuario.getRolUsuario() != null) {
            return Collections.singleton(
                new SimpleGrantedAuthority("ROLE_" + usuario.getRolUsuario().name())
            );
        }
        return Collections.emptyList();
    }

    @Override
    public String getPassword() {
        return usuario.getPassword(); // El password en texto plano de la BD
    }

    @Override
    public String getUsername() {
        return usuario.getNombre(); // Usamos nombre como username
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}