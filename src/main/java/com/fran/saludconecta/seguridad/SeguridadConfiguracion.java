package com.fran.saludconecta.seguridad;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration // Indica que esta clase contiene configuración para Spring
@EnableWebSecurity // Activa Spring Security en la aplicación web
public class SeguridadConfiguracion {

    @Bean // Spring usará este método para construir el filtro de seguridad
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            .csrf().disable() // Desactiva la protección CSRF (útil en desarrollo o si no usas tokens en formularios)

            .authorizeHttpRequests(auth -> auth
                // Estas rutas se permiten sin autenticación:
                .requestMatchers("/login", "/css/**", "/js/**").permitAll()
                // Todas las demás rutas requieren estar autenticado
                .anyRequest().authenticated()
            )
            
            .formLogin(form -> form
                // Vista personalizada de login
                .loginPage("/login")
                // URL que procesa el formulario de login (debe coincidir con th:action en login.html)
                .loginProcessingUrl("/login")
                // Redirección tras login exitoso
                .defaultSuccessUrl("/inicio", true)
                // Permite que todos accedan al formulario de login
                .permitAll()
            )

            .logout(logout -> logout
                // Redirección tras cerrar sesión
                .logoutSuccessUrl("/login?logout")
            )
            
            .httpBasic() // ✅ esto permite autenticación básica para Postman, curl, etc.
            ;

        // Devuelve la configuración construida
        return http.build();
    }
}
