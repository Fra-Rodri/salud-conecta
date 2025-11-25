package com.fran.saludconecta.calendario.controller;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CalendarioVistaController {
	
    @GetMapping("/calendario-vista") // método que se ejecuta cuando el usuario accede a la URL /pacientes en el navegador. Es una ruta HTTP GET.
    public String calendarioVista(Principal principal, Model model) {

        // Aquí obtén el nombre del usuario autenticado
        String usuarioActivo = principal.getName(); 
        model.addAttribute("usuarioActivo", usuarioActivo);

        return "calendario/calendario-ver"; // nombre del template, Le dice a Spring: “Después de ejecutar este método, muestra la plantilla pacientes.html”. No redirige a otra URL, simplemente renderiza el HTML que está en src/main/resources/templates/pacientes.html.
    }
}
