// package com.fran.saludconecta.seguridad;

// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.security.config.annotation.web.builders.HttpSecurity;
// import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
// import org.springframework.security.web.SecurityFilterChain;

// @Configuration // Indica que esta clase contiene configuración para Spring
// @EnableWebSecurity // Activa Spring Security en la aplicación web
// public class SeguridadConfiguracion {

//     @Bean // Spring usará este método para construir el filtro de seguridad
//     public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

//         http
//             .csrf().disable() // Desactiva la protección CSRF (útil en desarrollo o si no usas tokens en formularios)

//             .authorizeHttpRequests(auth -> auth
//                 // Estas rutas se permiten sin autenticación:
//                 .requestMatchers("/login", "/css/**", "/js/**").permitAll()
//                 // Todas las demás rutas requieren estar autenticado
//                 .anyRequest().authenticated()
//             )
            
//             .formLogin(form -> form
//                 // Vista personalizada de login
//                 .loginPage("/login")
//                 // URL que procesa el formulario de login (debe coincidir con th:action en login.html)
//                 .loginProcessingUrl("/login")
//                 // Redirección tras login exitoso
//                 .defaultSuccessUrl("/inicio", true)
//                 // Permite que todos accedan al formulario de login
//                 .permitAll()
//             )

//             .logout(logout -> logout
//                 // Redirección tras cerrar sesión
//                 .logoutSuccessUrl("/login?logout")
//             )
            
//             .httpBasic() // ✅ esto permite autenticación básica para Postman, curl, etc.
//             ;

//         // Devuelve la configuración construida
//         return http.build();
//     }
// }

package com.fran.saludconecta.seguridad;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SeguridadConfiguracion {

    @Autowired
    private UserDetailsService usuarioDetallesService; // Tu servicio

    @Bean
    public PasswordEncoder passwordEncoder() {
        // ⚠️ IMPORTANTE: NoOpPasswordEncoder = sin encriptación (solo para desarrollo/prueba)
        // En PRODUCCIÓN usar: return new BCryptPasswordEncoder();
        return NoOpPasswordEncoder.getInstance();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(usuarioDetallesService); // Le dices: "usa mi servicio"
        provider.setPasswordEncoder(passwordEncoder()); // Le dices: "compara passwords así"
        return provider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            .csrf().disable()
            .authenticationProvider(authenticationProvider()) // ← Aquí le pasas tu provider

            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/login", "/css/**", "/js/**").permitAll()
                .anyRequest().authenticated()
            )
            
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/inicio", true)
                .permitAll()
            )

            .logout(logout -> logout
                .logoutSuccessUrl("/login?logout")
            )
            
            .httpBasic();

        return http.build();
    }
}