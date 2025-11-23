package com.fran.saludconecta.usuario.mapper;

import org.jooq.DSLContext;

import com.fran.saludconecta.jooq.tables.Usuario;
import com.fran.saludconecta.jooq.tables.records.UsuarioRecord;
import com.fran.saludconecta.usuario.dto.UsuarioDTO;

public class UsuarioMapper {

    public static UsuarioDTO toDTO (UsuarioRecord record) {
        
        if (record == null) return null;
        
        return UsuarioDTO.builder()
                .id(record.get(Usuario.USUARIO.ID))
                .nombre(record.get(Usuario.USUARIO.NOMBRE))
                .email(record.get(Usuario.USUARIO.EMAIL))
                .password(record.get(Usuario.USUARIO.PASSWORD))
                .rolUsuario(record.get(Usuario.USUARIO.ROL))
                .negocioId(record.get(Usuario.USUARIO.NEGOCIO_ID))
                .fechaCreacion(record.get(Usuario.USUARIO.FECHA_CREACION))
                .fechaModificacion(record.get(Usuario.USUARIO.FECHA_MODIFICACION))
                .build();
    }

    public static UsuarioRecord fromDTO (UsuarioDTO dto, DSLContext dsl) {
        
        if (dto == null) return null;
        
        UsuarioRecord record = dsl.newRecord(Usuario.USUARIO);
        // record.setId(dto.getId());
        record.setNombre(dto.getNombre().trim());
        record.setEmail(dto.getEmail().trim());
        record.setPassword(dto.getPassword().trim());
        record.setRol(dto.getRolUsuario());
        record.setNegocioId(dto.getNegocioId());
        record.setFechaCreacion(dto.getFechaCreacion());
        record.setFechaModificacion(dto.getFechaModificacion());
        return record;
    }
}
