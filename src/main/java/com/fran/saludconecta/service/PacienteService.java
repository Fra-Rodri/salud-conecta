package com.fran.saludconecta.service;

import java.util.List;

import com.fran.saludconecta.dto.PacienteDTO;

public interface PacienteService {
	List<PacienteDTO> mostrarTodos();
	PacienteDTO mostrarPorId(Integer id);
	boolean crear(PacienteDTO dto);
	PacienteDTO modificar(Integer id, PacienteDTO dto);
	boolean borrar(Integer id);
}
