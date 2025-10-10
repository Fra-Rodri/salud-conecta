package com.fran.saludconecta.service;

import java.util.List;

import com.fran.saludconecta.dto.PacienteDTO;

public interface PacienteService {
	List<PacienteDTO> listarTodos();
	PacienteDTO obtenerPorId(Integer id);
	PacienteDTO crear(PacienteDTO dto);
	PacienteDTO actualizar(Integer id, PacienteDTO dto);
	void eliminar(Integer id);
}
