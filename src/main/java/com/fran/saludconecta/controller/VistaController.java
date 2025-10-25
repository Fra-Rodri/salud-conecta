package com.fran.saludconecta.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.fran.saludconecta.dto.InformeDTO;
import com.fran.saludconecta.dto.PacienteDTO;
import com.fran.saludconecta.service.PacienteService;


@Controller
public class VistaController {
	
	@Autowired
	private PacienteService service;
	//[VistaController] → usa → PacienteService → accede a datos → pasa al modelo → muestra HTML
		
	
	@GetMapping("/")
    public String redirigirAlLogin() {
        return "redirect:/login";
    }
	
	@GetMapping("/login")
	public String login() {
        return "login";
    }

    @GetMapping("/inicio")
    public String inicio() {
        return "inicio";
    }
    
    @GetMapping("/pacientes") // método que se ejecuta cuando el usuario accede a la URL /pacientes en el navegador. Es una ruta HTTP GET.
    public String pacientes(Model model) {
    	List<PacienteDTO> listaPacientes = service.mostrarTodos();
		model.addAttribute("pacientes", listaPacientes); // Esto envía la lista al HTML con el nombre "pacientes". En Thymeleaf, puedes acceder a esa lista con ${pacientes}.
        return "pacientes"; // nombre del template, Le dice a Spring: “Después de ejecutar este método, muestra la plantilla pacientes.html”. No redirige a otra URL, simplemente renderiza el HTML que está en src/main/resources/templates/pacientes.html.
    }
    
    @GetMapping("/pacientes/crear")
    public String mostrarFormularioCreacion(Model model) {
        model.addAttribute("paciente", new PacienteDTO());
        return "crear-paciente";
    }

    @PostMapping("/pacientes/crear")
    public String procesarCreacion(@ModelAttribute PacienteDTO dto) {
        service.crear(dto);
        return "redirect:/pacientes";
    }
    
    @GetMapping("/pacientes/editar/{id}")
    public String mostrarFormularioEdicion(@PathVariable Integer id, Model model) {
        PacienteDTO paciente = service.mostrarPorId(id);
        if (paciente != null) {
            model.addAttribute("paciente", paciente);
            return "editar-paciente";
        } else {
            return "redirect:/pacientes"; 
        }
    }
    
    @PostMapping("/pacientes/editar/{id}")
    public String procesarEdicion(@PathVariable Integer id, @ModelAttribute PacienteDTO dto) {
        service.modificar(id, dto);
        return "redirect:/pacientes";
    }
    
    
    @GetMapping("/pacientes/eliminar/{id}")
    public String mostrarConfirmacionEliminacion(@PathVariable Integer id, Model model) {
        PacienteDTO paciente = service.mostrarPorId(id);
        if (paciente != null) {
            model.addAttribute("paciente", paciente);
            return "confirmar-eliminacion";
        } else {
            return "redirect:/pacientes";
        }
    }

    @PostMapping("/pacientes/eliminar/{id}")
    public String procesarEliminacion(@PathVariable Integer id) {
        service.borrar(id);
        return "redirect:/pacientes";
    }
    
    @GetMapping("/pacientes/ver/{id}") 
    public String pacientesDetalles(@PathVariable Integer id, Model model) {
    	PacienteDTO elemento = service.mostrarPorId(id);
		model.addAttribute("elemento", elemento); 
        return "pacientes/pacientes-detalles"; 
        
    }
}
