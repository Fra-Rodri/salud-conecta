package com.fran.saludconecta.mapper;

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
}
