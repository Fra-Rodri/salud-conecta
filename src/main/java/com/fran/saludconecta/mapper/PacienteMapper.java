package com.fran.saludconecta.mapper;

import org.jooq.DSLContext;

import com.fran.saludconecta.dto.PacienteDTO;
import com.fran.saludconecta.jooq.tables.Paciente;
import com.fran.saludconecta.jooq.tables.records.PacienteRecord;

public class PacienteMapper {

	public static PacienteDTO toDTO(PacienteRecord record) {
		
        if (record == null) return null;
        
        return PacienteDTO.builder()
                .id(record.get(Paciente.PACIENTE.ID))
                .nombre(record.get(Paciente.PACIENTE.NOMBRE))
                .dni(record.get(Paciente.PACIENTE.DNI))
                .fechaNacimiento(record.get(Paciente.PACIENTE.FECHA_NACIMIENTO))
                .build();
    }
	
	public static PacienteRecord fromDTO(PacienteDTO dto, DSLContext dsl) {
		
		if (dto == null) return null;
		
		PacienteRecord record = dsl.newRecord(Paciente.PACIENTE);
//		record.setId(dto.getId());
		record.setNombre(dto.getNombre());
		record.setDni(dto.getDni());
		record.setFechaNacimiento(dto.getFechaNacimiento());
		return record;
	}
}
