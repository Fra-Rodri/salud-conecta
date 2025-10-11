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

    public List<PacientesRecord> findAll() {
        return dsl.selectFrom(Pacientes.PACIENTES).fetch();
    }

    public PacientesRecord findById(Integer id) {
        return dsl.selectFrom(Pacientes.PACIENTES)
                  .where(Pacientes.PACIENTES.ID.eq(id))
                  .fetchOne();
    }

    public PacientesRecord save(PacienteDTO dto) {
        var record = dsl.newRecord(Pacientes.PACIENTES);
        record.setNombre(dto.getNombre());
        record.setDni(dto.getDni());
        record.setFechaNacimiento(dto.getFechaNacimiento());
        record.store();
        return record;
    }

    public boolean delete(Integer id) {
        var record = findById(id);
        if (record != null) {
            record.delete();
            return true;
        }
        return false;
    }

    public PacientesRecord update(Integer id, PacienteDTO dto) {
        var record = findById(id);
        if (record != null) {
            record.setNombre(dto.getNombre());
            record.setDni(dto.getDni());
            record.setFechaNacimiento(dto.getFechaNacimiento());
            record.update();
        }
        return record;
    }
}
