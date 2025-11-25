package com.fran.saludconecta.cita.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.fran.saludconecta.cita.dto.CitaDTO;
import com.fran.saludconecta.jooq.tables.Cita;
import com.fran.saludconecta.jooq.tables.records.CitaRecord;

@Repository
public class CitaRepository {

    @Autowired
    private DSLContext dsl;

    public List<CitaRecord> obtenerTodos() {
        return dsl.selectFrom(Cita.CITA).orderBy(Cita.CITA.FECHA_CITA).fetch();
    }

    public CitaRecord obtenerPorId(Integer id) {
        return dsl.selectFrom(Cita.CITA)
                  .where(Cita.CITA.ID.eq(id))
                  .fetchOne();
    }

    public CitaRecord guardar(CitaRecord guardarRecord) {
        CitaRecord record = dsl.newRecord(Cita.CITA);
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

    public CitaRecord actualizar(Integer id, CitaDTO dto) {
        var record = obtenerPorId(id);
        if (record != null) {
            record.setPacienteId(dto.getPacienteId());
            record.setUsuarioId(dto.getUsuarioId());
            record.setFechaCita(dto.getFechaCita());
            record.setMotivo(dto.getMotivo());
            record.setEstado(dto.getEstado());
            // record.setFechaCreacion(dto.getFechaCreacion()); // no se sobrescribe la fecha de creación
            record.setFechaModificacion(LocalDateTime.now());
            record.update();
        }
        return record;
    }
}