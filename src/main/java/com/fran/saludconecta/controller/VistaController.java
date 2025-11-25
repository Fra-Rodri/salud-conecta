package com.fran.saludconecta.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.fran.saludconecta.usuario.dto.UsuarioDTO;
import com.fran.saludconecta.usuario.service.IUsuarioService;

@Controller
public class VistaController {

    @Autowired
    private IUsuarioService service;

    @GetMapping("/")
    public String redirigirAlLogin() {
        return "redirect:/login";
    }
	
	@GetMapping("/login")
	public String login() {
        return "login";
    }

    @GetMapping("/inicio")
    public String inicio(Principal principal, Model model) {

        // Aquí obtén el nombre del usuario autenticado
        String usuarioActivo = principal.getName(); 
        model.addAttribute("usuarioActivo", usuarioActivo);

        UsuarioDTO usuarioDto = null;
        try {
            usuarioDto = service.mostrarTodos().stream()
                    .filter(u -> usuarioActivo.equals(u.getNombre()))
                    .findFirst()
                    .orElse(null);
        } catch (Exception e) {
            usuarioDto = null;
        }

        if (usuarioDto != null) {
            model.addAttribute("usuario", usuarioDto);
        } else {
            // fallback: si no encontramos DTO, ponemos el nombre/email para renderizar al menos el nombre
            model.addAttribute("usuario", usuarioActivo);
        }


        return "inicio";
    }
}
