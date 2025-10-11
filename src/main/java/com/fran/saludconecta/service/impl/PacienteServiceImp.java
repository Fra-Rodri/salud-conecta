package com.fran.saludconecta.service.impl;

import java.util.List;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fran.saludconecta.dto.PacienteDTO;
import com.fran.saludconecta.jooq.tables.records.PacientesRecord;
import com.fran.saludconecta.mapper.PacienteMapper;
import com.fran.saludconecta.repository.PacienteRepository;
import com.fran.saludconecta.service.PacienteService;

@Service
public class PacienteServiceImp implements PacienteService{

	@Autowired
	private DSLContext dsl;
//	
//	/*
//	 * FALTA SABER:
//	 * 
//	 *  - DSL
//	 *  - FETCH()
//	 *  - MAP. RECORD
//	 */
//	
//	@Override
//	public List<PacienteDTO> listarTodos() {
//		return dsl.selectFrom(Pacientes.PACIENTES)
//				.fetch()
//				.map(record -> {
//					PacienteDTO dto = new PacienteDTO();
//					dto.setId(record.getId());
//					dto.setNombre(record.getNombre());
//					dto.setDni(record.getDni());
//					dto.setFechaNacimiento(record.getFechaNacimiento());
//					return dto;
//				});
//	}
//
//	@Override
//	public PacienteDTO obtenerPorId(Integer id) {
//		var record = dsl.selectFrom(Pacientes.PACIENTES)
//				.where(Pacientes.PACIENTES.ID.eq(id))
//				.fetchOne();
//		
//		if (record == null ) return null;
//		
//		return PacienteDTO.builder()
//						.id(record.getId())
//						.nombre(record.getNombre())
//						.dni(record.getDni())
//						.fechaNacimiento(record.getFechaNacimiento())
//						.build();
//		
////		return null;
//	}
//
//	/*
//	 * FALTA SABER:
//	 * 
//	 *  - .STORE
//	 */
//	@Override
//	public PacienteDTO crear(PacienteDTO dto) {
//	    var record = dsl.newRecord(Pacientes.PACIENTES);
//	    record.setNombre(dto.getNombre());
//	    record.setDni(dto.getDni());
//	    record.setFechaNacimiento(dto.getFechaNacimiento());
//	    record.store(); // inserta y actualiza el ID
//	    dto.setId(record.getId());
//	    return dto;
//	}
//
//	/*
//	 * FALTA SABER:
//	 * 
//	 *  - UPDATE()
//	 */
//	@Override
//	public PacienteDTO actualizar(Integer id, PacienteDTO dto) {
//	    var record = dsl.fetchOne(Pacientes.PACIENTES, Pacientes.PACIENTES.ID.eq(id));
//	    if (record != null) {
//	        record.setNombre(dto.getNombre());
//	        record.setDni(dto.getDni());
//	        record.setFechaNacimiento(dto.getFechaNacimiento());
//	        record.update();
//	        dto.setId(id);
//	        return dto;
//	    }
//	    return null;
//	}
//
//	/*
//	 * FALTA SABER:
//	 * 
//	 *  - EXECUTE()
//	 */
//	@Override
//	public boolean eliminar(Integer id) {
////	    dsl.deleteFrom(Pacientes.PACIENTES)
////	       .where(Pacientes.PACIENTES.ID.eq(id))
////	       .execute();
//		
//		
//		var record = dsl.fetchOne(Pacientes.PACIENTES, Pacientes.PACIENTES.ID.eq(id));
//		
//		if (record != null) {
//			record.delete();
//			return true;
//		}
//		
//		return false;
//	}
	
	
	
	
	  @Autowired
	    private PacienteRepository repository;

	    @Override
	    public List<PacienteDTO> mostrarTodos() {
	        return repository.obtenerTodos()
	                         .stream()
	                         .map(PacienteMapper::toDTO)
	                         .toList();
	    }

	    @Override
	    public PacienteDTO mostrarPorId(Integer id) {
	        return PacienteMapper.toDTO(repository.obtenerPorId(id));
	    }

	    @Override
	    public boolean crear(PacienteDTO dto) {
	    	PacientesRecord guardarRecord = PacienteMapper.fromDTO(dto, dsl);
	    	PacientesRecord comprobarRecord = repository.obtenerPorId(guardarRecord.getId());
	    	
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
