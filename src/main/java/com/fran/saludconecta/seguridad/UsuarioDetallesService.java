package com.fran.saludconecta.seguridad;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.fran.saludconecta.usuario.service.IUsuarioService;

@Service
public class UsuarioDetallesService implements UserDetailsService {

    @Autowired
    private IUsuarioService usuarioService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Buscar usuario en BD por NOMBRE (o podrías usar email)
        var usuarios = usuarioService.mostrarTodos(); // Obtiene TODOS los usuarios
        var usuario = usuarios.stream()
            .filter(u -> u.getNombre().equals(username)) // Busca por nombre
            .findFirst()
            .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));
        
        // Devuelve el usuario adaptado a UserDetails
        return new UsuarioDetalles(usuario);
    }
}