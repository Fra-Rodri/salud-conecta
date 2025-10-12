package com.fran.saludconecta.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.fran.saludconecta.dto.PacienteDTO;
import com.fran.saludconecta.service.PacienteService;


@Controller
public class VistaController {
	
	@Autowired
	private PacienteService pacienteService;
	//[VistaController] → usa → PacienteService → accede a datos → pasa al modelo → muestra HTML
	
	
	/*
	 * Estos métodos son los iniciales
	 * usando vistas simples antes de css y js
	 * Lo que hacen son siempre lo mismo, 
	 * busca su return "login" por ejemplo en "/templates"
	 */

//	@GetMapping("/")
//    public String redirigirAlLogin() {
//        return "redirect:/login";
//    }
//	
//	@GetMapping("/login")
//	public String login() {
//        return "login";
//    }
//
//    @GetMapping("/inicio")
//    public String inicio() {
//        return "inicio";
//    }
	
	
	
	@GetMapping("/")
    public String redirigirAlLogin() {
        return "redirect:/login2";
    }
	
	@GetMapping("/login2")
	public String login() {
        return "login2";
    }

    @GetMapping("/inicio2")
    public String inicio() {
        return "inicio2";
    }
    
    @GetMapping("/pacientes2")
    public String pacientes(Model model) {
    	List<PacienteDTO> lista = pacienteService.mostrarTodos();
		model.addAttribute("pacientes", lista);
        return "pacientes2";
    }
    
    
    @GetMapping("/pacientes2/editar/{id}")
    public String mostrarFormularioEdicion(@PathVariable Integer id, Model model) {
        PacienteDTO paciente = pacienteService.mostrarPorId(id);
        if (paciente != null) {
            model.addAttribute("paciente", paciente);
            return "editar-paciente";// nombre del archivo HTML en /templates
        } else {
            return "redirect:/pacientes2"; // o mostrar una vista de error si prefieres
        }
    }
    
    @GetMapping("/pacientes2/eliminar/{id}")
    public String mostrarConfirmacionEliminacion(@PathVariable Integer id, Model model) {
        PacienteDTO paciente = pacienteService.mostrarPorId(id);
        if (paciente != null) {
            model.addAttribute("paciente", paciente);
            return "confirmar-eliminacion";
        } else {
            return "redirect:/pacientes2";
        }
    }
    
    @PostMapping("/pacientes2/editar/{id}")
    public String procesarEdicion(@PathVariable Integer id, @ModelAttribute PacienteDTO dto) {
        pacienteService.modificar(id, dto);
        return "redirect:/pacientes2";
    }

    @PostMapping("/pacientes2/eliminar/{id}")
    public String procesarEliminacion(@PathVariable Integer id) {
        pacienteService.borrar(id);
        return "redirect:/pacientes2";
    }
	
    @GetMapping("/pacientes2/crear")
    public String mostrarFormularioCreacion(Model model) {
        model.addAttribute("paciente", new PacienteDTO());
        return "crear-paciente";
    }

    @PostMapping("/pacientes2/crear")
    public String procesarCreacion(@ModelAttribute PacienteDTO dto) {
        pacienteService.crear(dto);
        return "redirect:/pacientes2";
    }
    
//    @GetMapping("/calendario")
//    public String mostrarCalendario(Model model, Principal principal) {
//        Usuario usuario = usuarioService.obtenerPorUsername(principal.getName());
//        List<CitaDTO> citas = citaService.obtenerPorMedico(usuario.getId());
//        model.addAttribute("citas", citas);
//        return "calendario";
//    }
    
    @GetMapping("/calendario")
    public String mostrarCalendario() {
        return "calendario";
    }
}
