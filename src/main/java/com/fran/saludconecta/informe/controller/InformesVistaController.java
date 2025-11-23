package com.fran.saludconecta.informe.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.fran.saludconecta.dto.InformeDTO;
import com.fran.saludconecta.informe.service.InformeService;

@Controller
public class InformesVistaController {

	@Autowired
	private InformeService service;
    
    @GetMapping("/informes-lista") // método que se ejecuta cuando el usuario accede a la URL /pacientes en el navegador. Es una ruta HTTP GET.
    public String informesLista(Model model) {
    	List<InformeDTO> lista = service.mostrarTodos();
		model.addAttribute("informes", lista); // Esto envía la lista al HTML con el nombre "pacientes". En Thymeleaf, puedes acceder a esa lista con ${pacientes}.
        return "informes/informes-lista"; // nombre del template, Le dice a Spring: “Después de ejecutar este método, muestra la plantilla pacientes.html”. No redirige a otra URL, simplemente renderiza el HTML que está en src/main/resources/templates/pacientes.html.
    }
    
    @GetMapping("/informes/ver/{id}") 
    public String informesDetalles(@PathVariable Integer id, Model model) {
    	InformeDTO elemento = service.mostrarPorId(id);
		model.addAttribute("elemento", elemento); 
        return "informes/informes-detalles"; 
        
    }
}
