package com.fran.saludconecta.paciente.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.fran.saludconecta.paciente.dto.PacienteDTO;
import com.fran.saludconecta.paciente.service.IPacienteService;

import jakarta.validation.Valid;


@Controller
public class PacienteVistaController {
	
	@Autowired
	private IPacienteService service;
	//[VistaController] → usa → PacienteService → accede a datos → pasa al modelo → muestra HTML
		
	
	// @GetMapping("/")
    // public String redirigirAlLogin() {
    //     return "redirect:/login";
    // }
	
	// @GetMapping("/login")
	// public String login() {
    //     return "login";
    // }

    // @GetMapping("/inicio")
    // public String inicio(Principal principal, Model model) {
    //     String usuario = principal.getName(); // Aquí obtén el nombre del usuario autenticado
    //     model.addAttribute("usuario", usuario);
    //     return "inicio";
    // }
    
    @GetMapping("/paciente-lista") // método que se ejecuta cuando el usuario accede a la URL /pacientes en el navegador. Es una ruta HTTP GET.
    public String pacientes(Principal principal, Model model) {

        // Aquí obtén el nombre del usuario autenticado
        String usuarioActivo = principal.getName(); 
        model.addAttribute("usuarioActivo", usuarioActivo);

    	List<PacienteDTO> listaPacientes = service.mostrarTodos();
		model.addAttribute("pacientes", listaPacientes); // Esto envía la lista al HTML con el nombre "pacientes". En Thymeleaf, puedes acceder a esa lista con ${pacientes}.
        return "paciente/paciente-lista"; // nombre del template, Le dice a Spring: “Después de ejecutar este método, muestra la plantilla pacientes.html”. No redirige a otra URL, simplemente renderiza el HTML que está en src/main/resources/templates/pacientes.html.
    }

    @GetMapping("/paciente/ver/{id}") 
    public String pacientesDetalles(Principal principal, @PathVariable Integer id, Model model) {
        
        // Aquí obtén el nombre del usuario autenticado
        String usuarioActivo = principal.getName(); 
        model.addAttribute("usuarioActivo", usuarioActivo);

    	PacienteDTO elemento = service.mostrarDetallesPorId(id);
		model.addAttribute("paciente", elemento); 
        return "paciente/paciente-detalle"; 
    }
    
    @GetMapping("/paciente/crear")
    public String mostrarFormularioCreacion(Principal principal, Model model) {
        
        // Aquí obtén el nombre del usuario autenticado
        String usuarioActivo = principal.getName(); 
        model.addAttribute("usuarioActivo", usuarioActivo);

        model.addAttribute("paciente", new PacienteDTO());
        return "paciente/paciente-crear";
    }

    @PostMapping("/paciente/crear")
    public String procesarCreacion(Principal principal, @Valid @ModelAttribute("paciente") PacienteDTO dto, BindingResult result, Model model) {
        
        // Aquí obtén el nombre del usuario autenticado
        String usuarioActivo = principal.getName(); 
        model.addAttribute("usuarioActivo", usuarioActivo);

        // Si hay errores de validación estándar (NotBlank, Size, ...)
        if (result.hasErrors()) {
            return "paciente/paciente-crear"; // vuelve a mostrar el formulario con errores
        }

        // Comprobar existencia: suponiendo que el servicio tiene un método para ello.
        // Si no existe, puedes usar service.mostrarTodos().stream().anyMatch(...)
        boolean creado = service.crear(dto); // según tu impl. actual devuelve boolean
        if (!creado) {
            // Asumiendo que la comprobación está basada en dni:
            result.rejectValue("dni", "error.dni", "Ya existe un paciente con ese DNI");
            return "paciente/paciente-crear";
        }

        service.crear(dto);
        return "redirect:/paciente-lista";
    }
    
    @GetMapping("/paciente/editar/{id}")
    public String mostrarFormularioEdicion(Principal principal, @PathVariable Integer id, Model model) {
        
        // Aquí obtén el nombre del usuario autenticado
        String usuarioActivo = principal.getName(); 
        model.addAttribute("usuarioActivo", usuarioActivo);

        PacienteDTO paciente = service.mostrarPorId(id);
        if (paciente != null) {
            model.addAttribute("paciente", paciente);
            return "paciente/paciente-editar";
        } else {
            return "redirect:/paciente-lista";
        }
    }
    
    @PostMapping("/paciente/editar/{id}")
    public String procesarEdicion(Principal principal, @PathVariable Integer id, @Valid @ModelAttribute("paciente") PacienteDTO dto, BindingResult result, Model model) {
        
        // Aquí obtén el nombre del usuario autenticado
        String usuarioActivo = principal.getName(); 
        model.addAttribute("usuarioActivo", usuarioActivo);

        // Si hay errores de validación estándar (NotBlank, Size, ...)
        if (result.hasErrors()) {
            return "paciente/paciente-editar"; // vuelve a mostrar el formulario con errores
        }

        // Comprobar existencia: suponiendo que el servicio tiene un método para ello.
        // Si no existe, puedes usar service.mostrarTodos().stream().anyMatch(...)
        boolean creado = service.crear(dto); // según tu impl. actual devuelve boolean
        if (!creado) {
            // Asumiendo que la comprobación está basada en dni:
            result.rejectValue("dni", "error.dni", "Ya existe un paciente con ese DNI");
            return "paciente/paciente-editar";
        }

        service.modificar(id, dto);
        return "redirect:/paciente-lista";
    }
    
    
    @GetMapping("/paciente/eliminar/{id}")
    public String mostrarConfirmacionEliminacion(Principal principal, @PathVariable Integer id, Model model) {
        
        // Aquí obtén el nombre del usuario autenticado
        String usuarioActivo = principal.getName(); 
        model.addAttribute("usuarioActivo", usuarioActivo);
        
        PacienteDTO paciente = service.mostrarPorId(id);
        if (paciente != null) {
            model.addAttribute("paciente", paciente);
            return "paciente/paciente-eliminar-confirmar";
        } else {
            return "redirect:/paciente-lista";
        }
    }

    @PostMapping("/paciente/eliminar/{id}")
    public String procesarEliminacion(Principal principal, @PathVariable Integer id, Model model) {
        
        // Aquí obtén el nombre del usuario autenticado
        String usuarioActivo = principal.getName(); 
        model.addAttribute("usuarioActivo", usuarioActivo);

        service.borrar(id);
        return "redirect:/paciente-lista";
    }
}
