package com.fran.saludconecta.repository;

import java.util.List;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.fran.saludconecta.dto.PacienteDTO;
import com.fran.saludconecta.jooq.tables.Paciente;
import com.fran.saludconecta.jooq.tables.records.PacienteRecord;

@Repository
public class PacienteRepository {
	
	@Autowired
    private DSLContext dsl;

    public List<PacienteRecord> obtenerTodos() {
        return dsl.selectFrom(Paciente.PACIENTE).fetch();
    }

    public PacienteRecord obtenerPorId(Integer id) {
        return dsl.selectFrom(Paciente.PACIENTE)
                  .where(Paciente.PACIENTE.ID.eq(id))
                  .fetchOne();
    }
    
    public PacienteRecord guardar(PacienteRecord guardarRecord) {
    	PacienteRecord record = dsl.newRecord(Paciente.PACIENTE);
        record = guardarRecord;
        record.store();
        return record;
    }

    public boolean eliminar(Integer id) {
        var record = obtenerPorId(id);
        if (record != null) {
            record.delete();
            return true;
        }
        return false;
    }

    public PacienteRecord actualizar(Integer id, PacienteDTO dto) {
        var record = obtenerPorId(id);
        if (record != null) {
            record.setNombre(dto.getNombre());
            record.setDni(dto.getDni());
            record.setFechaNacimiento(dto.getFechaNacimiento());
            record.update();
        }
        return record;
    }
}
