package com.fran.saludconecta.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import com.fran.saludconecta.dto.PacienteDTO;
import com.fran.saludconecta.service.PacienteService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class VistaPacienteController {

	@Autowired
	private PacienteService pacienteService;
	
	@GetMapping("/vista/pacientes")
	public String mostrarPacientes(Model model) {
		List<PacienteDTO> lista = pacienteService.listarTodos();
		model.addAttribute("pacientes", lista);
		return "pacientes";
	}
	
	@GetMapping("/vista2/pacientes")
	public String mostrarPacientes2(Model model) {
		List<PacienteDTO> lista = pacienteService.listarTodos();
		model.addAttribute("pacientes", lista);
		return "pacientes2";
	}
	
}
