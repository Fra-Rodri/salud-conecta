package com.fran.saludconecta.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.fran.saludconecta.service.InformeService;

@Controller
public class CalendarioVistaController {
	
	@Autowired
	private InformeService service;
    
    @GetMapping("/calendario-vista") // método que se ejecuta cuando el usuario accede a la URL /pacientes en el navegador. Es una ruta HTTP GET.
    public String calendarioVista(Model model) {
        return "calendario/calendario-ver"; // nombre del template, Le dice a Spring: “Después de ejecutar este método, muestra la plantilla pacientes.html”. No redirige a otra URL, simplemente renderiza el HTML que está en src/main/resources/templates/pacientes.html.
    }
}
