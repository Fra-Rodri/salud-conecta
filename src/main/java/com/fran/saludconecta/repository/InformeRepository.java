package com.fran.saludconecta.repository;

import java.util.List;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.fran.saludconecta.jooq.tables.Informe;
import com.fran.saludconecta.jooq.tables.records.InformeRecord;

@Repository
public class InformeRepository {

	@Autowired
    private DSLContext dsl;
	
    public List<InformeRecord> obtenerTodos() {
        return dsl.selectFrom(Informe.INFORME).orderBy(Informe.INFORME.NOMBRE_PACIENTE).fetch();
    }

    public InformeRecord obtenerPorId(Integer id) {
        return dsl.selectFrom(Informe.INFORME)
                  .where(Informe.INFORME.ID.eq(id))
                  .fetchOne();
    }
}
