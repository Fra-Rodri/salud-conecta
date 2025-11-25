package com.fran.saludconecta.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.fran.saludconecta.informe.service.IInformeService;
import com.fran.saludconecta.negocio.service.INegocioService;
import com.fran.saludconecta.paciente.service.IPacienteService;
import com.fran.saludconecta.usuario.dto.UsuarioDTO;
import com.fran.saludconecta.usuario.service.IUsuarioService;

@Controller
public class VistaController {

    @Autowired
    private IUsuarioService usuarioService;

    @Autowired
    private IPacienteService pacienteService;

    @Autowired
    private IInformeService informeService;

    @Autowired
    private INegocioService negocioService;

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

        // Intenta obtener el DTO completo del usuario activo para mostrar más detalles en el perfil
        UsuarioDTO usuarioDto = null;
        try {
            usuarioDto = usuarioService.mostrarTodos().stream()
                    .filter(u -> usuarioActivo.equals(u.getNombre()))
                    .findFirst()
                    .orElse(null);
        } catch (Exception e) {
            usuarioDto = null;
        }
        model.addAttribute("usuario", usuarioDto);

        // Calculos rápidos para el dashboard
        Integer totalUsuarios = usuarioService.mostrarTodos().size();
        model.addAttribute("totalUsuarios", totalUsuarios);

        Integer totalNegocios = negocioService.mostrarTodos().size();
        model.addAttribute("totalNegocios", totalNegocios);

        Integer totalPacientes = pacienteService.mostrarTodos().size();
        model.addAttribute("totalPacientes", totalPacientes);

        Integer totalInformes = informeService.mostrarTodos().size();
        model.addAttribute("totalInformes", totalInformes);

        return "inicio";
    }
}
