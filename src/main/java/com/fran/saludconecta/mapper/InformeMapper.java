package com.fran.saludconecta.mapper;

import org.jooq.DSLContext;

import com.fran.saludconecta.dto.InformeDTO;
import com.fran.saludconecta.jooq.tables.Informe;
import com.fran.saludconecta.jooq.tables.records.InformeRecord;

public class InformeMapper {

public static InformeDTO toDTO(InformeRecord record) {
		
        if (record == null) return null;
        
        return InformeDTO.builder()
                .id(record.get(Informe.INFORME.ID))
                .usuarioId(record.get(Informe.INFORME.USUARIO_ID))
                .nombreUsuario(record.get(Informe.INFORME.NOMBRE_USUARIO))
                .pacienteId(record.get(Informe.INFORME.PACIENTE_ID))
                .nombrePaciente(record.get(Informe.INFORME.NOMBRE_PACIENTE))
                .contenido(record.get(Informe.INFORME.CONTENIDO))
                .fechaCreacion(record.get(Informe.INFORME.FECHA_CREACION))
                .fechaModificacion(record.get(Informe.INFORME.FECHA_MODIFICACION))
                .build();
    }
	
	public static InformeRecord fromDTO(InformeDTO dto, DSLContext dsl) {
		
		if (dto == null) return null;
		
		InformeRecord record = dsl.newRecord(Informe.INFORME);
//		record.setId(dto.getId());
		record.setUsuarioId(dto.getUsuarioId());
	    record.setNombreUsuario(dto.getNombreUsuario().trim());
	    record.setPacienteId(dto.getPacienteId());
	    record.setNombrePaciente(dto.getNombrePaciente().trim());
	    record.setContenido(dto.getContenido().trim());
	    record.setFechaCreacion(dto.getFechaCreacion());
	    record.setFechaModificacion(dto.getFechaModificacion());
		return record;
	}
}
