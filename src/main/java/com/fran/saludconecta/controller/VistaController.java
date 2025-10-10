package com.fran.saludconecta.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.fran.saludconecta.dto.PacienteDTO;
import com.fran.saludconecta.service.PacienteService;


@Controller
public class VistaController {

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
	
	@Autowired
	private PacienteService pacienteService;
	
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
    	List<PacienteDTO> lista = pacienteService.listarTodos();
		model.addAttribute("pacientes", lista);
        return "pacientes2";
    }
	
}
