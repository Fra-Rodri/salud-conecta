package com.fran.saludconecta.mapper;

import org.jooq.DSLContext;

import com.fran.saludconecta.dto.PacienteDTO;
import com.fran.saludconecta.jooq.tables.Pacientes;
import com.fran.saludconecta.jooq.tables.records.PacientesRecord;

public class PacienteMapper {

	public static PacienteDTO toDTO(PacientesRecord record) {
		
        if (record == null) return null;
        
        return PacienteDTO.builder()
                .id(record.get(Pacientes.PACIENTES.ID))
                .nombre(record.get(Pacientes.PACIENTES.NOMBRE))
                .dni(record.get(Pacientes.PACIENTES.DNI))
                .fechaNacimiento(record.get(Pacientes.PACIENTES.FECHA_NACIMIENTO))
                .build();
    }
	
	public static PacientesRecord fromDTO(PacienteDTO dto, DSLContext dsl) {
		
		if (dto == null) return null;
		
		PacientesRecord record = dsl.newRecord(Pacientes.PACIENTES);
		record.setId(dto.getId());
		record.setNombre(dto.getNombre());
		record.setDni(dto.getDni());
		record.setFechaNacimiento(dto.getFechaNacimiento());
		return record;
	}
}
