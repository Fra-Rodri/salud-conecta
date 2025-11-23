package com.fran.saludconecta.informe.service;

import java.util.List;

import com.fran.saludconecta.dto.InformeDTO;

public interface InformeService {
	List<InformeDTO> mostrarTodos();
	InformeDTO mostrarPorId(Integer id);
//	boolean crear(PacienteDTO dto);
//	PacienteDTO modificar(Integer id, PacienteDTO dto);
//	boolean borrar(Integer id);

}
