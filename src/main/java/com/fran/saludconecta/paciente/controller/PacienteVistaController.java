package com.fran.saludconecta.paciente.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.fran.saludconecta.paciente.dto.PacienteDTO;
import com.fran.saludconecta.paciente.service.IPacienteService;


@Controller
public class PacienteVistaController {
	
	@Autowired
	private IPacienteService service;
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
    public String inicio(Principal principal, Model model) {
        String usuario = principal.getName(); // Aquí obtén el nombre del usuario autenticado
        model.addAttribute("usuario", usuario);
        return "inicio";
    }
    
    @GetMapping("/pacientes") // método que se ejecuta cuando el usuario accede a la URL /pacientes en el navegador. Es una ruta HTTP GET.
    public String pacientes(Principal principal, Model model) {
        String usuario = principal.getName(); // Aquí obtén el nombre del usuario autenticado
        model.addAttribute("usuario", usuario);

    	List<PacienteDTO> listaPacientes = service.mostrarTodos();
		model.addAttribute("pacientes", listaPacientes); // Esto envía la lista al HTML con el nombre "pacientes". En Thymeleaf, puedes acceder a esa lista con ${pacientes}.
        return "pacientes"; // nombre del template, Le dice a Spring: “Después de ejecutar este método, muestra la plantilla pacientes.html”. No redirige a otra URL, simplemente renderiza el HTML que está en src/main/resources/templates/pacientes.html.
    }
    
    @GetMapping("/pacientes/crear")
    public String mostrarFormularioCreacion(Principal principal, Model model) {
        String usuario = principal.getName(); // Aquí obtén el nombre del usuario autenticado
        model.addAttribute("usuario", usuario);
        model.addAttribute("paciente", new PacienteDTO());
        return "crear-paciente";
    }

    @PostMapping("/pacientes/crear")
    public String procesarCreacion(Principal principal, @ModelAttribute PacienteDTO dto, Model model) {
        String usuario = principal.getName(); // Aquí obtén el nombre del usuario autenticado
        model.addAttribute("usuario", usuario);
        service.crear(dto);
        return "redirect:/pacientes";
    }
    
    @GetMapping("/pacientes/editar/{id}")
    public String mostrarFormularioEdicion(Principal principal, @PathVariable Integer id, Model model) {
        String usuario = principal.getName(); // Aquí obtén el nombre del usuario autenticado
        model.addAttribute("usuario", usuario);
        PacienteDTO paciente = service.mostrarPorId(id);
        if (paciente != null) {
            model.addAttribute("paciente", paciente);
            return "editar-paciente";
        } else {
            return "redirect:/pacientes"; 
        }
    }
    
    @PostMapping("/pacientes/editar/{id}")
    public String procesarEdicion(Principal principal, @PathVariable Integer id, @ModelAttribute PacienteDTO dto, Model model) {
        String usuario = principal.getName(); // Aquí obtén el nombre del usuario autenticado
        model.addAttribute("usuario", usuario);
        service.modificar(id, dto);
        return "redirect:/pacientes";
    }
    
    
    @GetMapping("/pacientes/eliminar/{id}")
    public String mostrarConfirmacionEliminacion(Principal principal, @PathVariable Integer id, Model model) {
        String usuario = principal.getName(); // Aquí obtén el nombre del usuario autenticado
        model.addAttribute("usuario", usuario);
        PacienteDTO paciente = service.mostrarPorId(id);
        if (paciente != null) {
            model.addAttribute("paciente", paciente);
            return "confirmar-eliminacion";
        } else {
            return "redirect:/pacientes";
        }
    }

    @PostMapping("/pacientes/eliminar/{id}")
    public String procesarEliminacion(Principal principal, @PathVariable Integer id, Model model) {
        String usuario = principal.getName(); // Aquí obtén el nombre del usuario autenticado
        model.addAttribute("usuario", usuario);
        service.borrar(id);
        return "redirect:/pacientes";
    }
    
    @GetMapping("/pacientes/ver/{id}") 
    public String pacientesDetalles(Principal principal, @PathVariable Integer id, Model model) {
        String usuario = principal.getName(); // Aquí obtén el nombre del usuario autenticado
        model.addAttribute("usuario", usuario);
    	PacienteDTO elemento = service.mostrarDetallesPorId(id);
		model.addAttribute("paciente", elemento); 
        return "pacientes/pacientes-detalles"; 
    }
}
