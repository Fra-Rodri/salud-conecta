package com.fran.saludconecta.service.impl;

import java.util.List;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fran.saludconecta.dto.InformeDTO;
import com.fran.saludconecta.mapper.InformeMapper;
import com.fran.saludconecta.repository.InformeRepository;
import com.fran.saludconecta.service.InformeService;

@Service
public class InformeServiceImpl implements InformeService{

	@Autowired
	private DSLContext dsl;

	@Autowired
	private InformeRepository repository;
	
	@Override
	public List<InformeDTO> mostrarTodos() {
		return repository.obtenerTodos().stream().map(InformeMapper::toDTO).toList();
	}

	@Override
	public InformeDTO mostrarPorId(Integer id) {
		return InformeMapper.toDTO(repository.obtenerPorId(id));
	}

}
