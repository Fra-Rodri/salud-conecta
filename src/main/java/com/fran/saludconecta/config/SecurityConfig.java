package com.fran.saludconecta.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//		http
//        .csrf().disable() // ← esto permite POST sin token CSRF
//        .authorizeHttpRequests(auth -> auth
//            .anyRequest().authenticated()
//        )
//        .httpBasic(); // ← mantiene autenticación básica
		
//		http
//	    .csrf().disable()
//	    .authorizeHttpRequests(auth -> auth
//	        .requestMatchers("/login", "/css/**", "/js/**").permitAll()
//	        .anyRequest().authenticated()
//	    )
//	    .formLogin(form -> form
//	        .loginPage("/login")
//	        .defaultSuccessUrl("/inicio", true)
//	        .permitAll()
//	    )
//	    .logout(logout -> logout
//	        .logoutSuccessUrl("/login?logout")
//	    );
		
		http
        .csrf().disable()
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/login2", "/css/**", "/js/**").permitAll()
            .anyRequest().authenticated()
        )
        .formLogin(form -> form
            .loginPage("/login2") // ← usa login2 como vista personalizada
            .loginProcessingUrl("/login") // ← esto también es clave
            .defaultSuccessUrl("/inicio2", true) // ← redirige tras login exitoso
            .permitAll()
        )
        .logout(logout -> logout
            .logoutSuccessUrl("/login2?logout") // ← redirige tras logout
        );

		return http.build();
	}

}
