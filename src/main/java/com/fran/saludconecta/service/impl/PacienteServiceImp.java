package com.fran.saludconecta.service.impl;

import java.util.List;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fran.saludconecta.dto.PacienteDTO;
import com.fran.saludconecta.jooq.tables.records.PacienteRecord;
import com.fran.saludconecta.mapper.PacienteMapper;
import com.fran.saludconecta.repository.PacienteRepository;
import com.fran.saludconecta.service.PacienteService;

@Service
public class PacienteServiceImp implements PacienteService {

	@Autowired
	private DSLContext dsl;

	@Autowired
	private PacienteRepository repository;

	@Override
	public List<PacienteDTO> mostrarTodos() {
		return repository.obtenerTodos().stream().map(PacienteMapper::toDTO).toList();
	}

	@Override
	public PacienteDTO mostrarPorId(Integer id) {
		return PacienteMapper.toDTO(repository.obtenerPorId(id));
	}

	@Override
	public boolean crear(PacienteDTO dto) {
		PacienteRecord guardarRecord = PacienteMapper.fromDTO(dto, dsl);
		PacienteRecord comprobarRecord = repository.obtenerPorId(guardarRecord.getId());

		if (comprobarRecord == null) {
			repository.guardar(guardarRecord);
			dto.setId(guardarRecord.getId());
			return true;
		}

		return false;
	}

	@Override
	public PacienteDTO modificar(Integer id, PacienteDTO dto) {
		var record = repository.actualizar(id, dto);
		return PacienteMapper.toDTO(record);
	}

	@Override
	public boolean borrar(Integer id) {
		return repository.eliminar(id);
	}
}
