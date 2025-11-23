package com.fran.saludconecta.informe.service;

import java.util.List;

import com.fran.saludconecta.informe.dto.InformeDTO;

public interface IInformeService {
	List<InformeDTO> mostrarTodos();
	InformeDTO mostrarPorId(Integer id);
//	boolean crear(PacienteDTO dto);
//	PacienteDTO modificar(Integer id, PacienteDTO dto);
//	boolean borrar(Integer id);

}
