package com.fran.saludconecta.repository;

import java.util.List;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.fran.saludconecta.dto.PacienteDTO;
import com.fran.saludconecta.jooq.tables.Pacientes;
import com.fran.saludconecta.jooq.tables.records.PacientesRecord;

@Repository
public class PacienteRepository {
	
	@Autowired
    private DSLContext dsl;

    public List<PacientesRecord> obtenerTodos() {
        return dsl.selectFrom(Pacientes.PACIENTES).fetch();
    }

    public PacientesRecord obtenerPorId(Integer id) {
        return dsl.selectFrom(Pacientes.PACIENTES)
                  .where(Pacientes.PACIENTES.ID.eq(id))
                  .fetchOne();
    }

//    public PacientesRecord guardar(PacienteDTO dto) {
//    	PacientesRecord record = dsl.newRecord(Pacientes.PACIENTES);
//        record.setNombre(dto.getNombre());
//        record.setDni(dto.getDni());
//        record.setFechaNacimiento(dto.getFechaNacimiento());
//        record.store();
//        return record;
//    }
    
    public PacientesRecord guardar(PacientesRecord guardarRecord) {
    	PacientesRecord record = dsl.newRecord(Pacientes.PACIENTES);
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

    public PacientesRecord actualizar(Integer id, PacienteDTO dto) {
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
