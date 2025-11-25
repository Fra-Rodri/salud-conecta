package com.fran.saludconecta.cita.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fran.saludconecta.cita.dto.CitaDTO;
import com.fran.saludconecta.cita.mapper.CitaMapper;
import com.fran.saludconecta.cita.repository.CitaRepository;
import com.fran.saludconecta.jooq.tables.records.CitaRecord;

@Service
public class CitaServiceImpl implements ICitaService {

    @Autowired
    private DSLContext dsl;

    @Autowired
    private CitaRepository repository;

    @Override
    public List<CitaDTO> mostrarTodos() {
        return repository.obtenerTodos().stream().map(CitaMapper::toDTO).toList();
    }

    @Override
    public CitaDTO mostrarPorId(Integer id) {
        return CitaMapper.toDTO(repository.obtenerPorId(id));
    }

    @Override
    public CitaDTO mostrarDetallesPorId(Integer id) {
        // Si en el futuro se añaden joins / datos extra, añadelos aquí.
        // Ahora devolvemos el DTO estándar (coincide con otras implementaciones del proyecto).
        return mostrarPorId(id);
    }

    @Override
    public boolean crear(CitaDTO dto) {
        CitaRecord guardarRecord = CitaMapper.fromDTO(dto, dsl);
        CitaRecord comprobarRecord = repository.obtenerPorId(guardarRecord.getId());

        if (comprobarRecord == null) {
            repository.guardar(guardarRecord);
            dto.setId(guardarRecord.getId());
            return true;
        }
        return false;
    }

    @Override
    public boolean comprobarCrear(CitaDTO dto) {
        // Validaciones mínimas: dto no nulo y campos requeridos
        if (dto == null) return false;
        if (dto.getFechaCita() == null || dto.getPacienteId() == null || dto.getUsuarioId() == null) return false;

        // Creación: si id es null se permite crear.
        if (dto.getId() == null) return true;

        // Edición: solo permitimos si el id existe en BBDD
        var record = repository.obtenerPorId(dto.getId());
        return record != null;
    }

    @Override
    public CitaDTO modificar(Integer id, CitaDTO dto) {
        if (dto.getNombrePaciente() != null) dto.setNombrePaciente(dto.getNombrePaciente().trim());
        if (dto.getNombreUsuario() != null) dto.setNombreUsuario(dto.getNombreUsuario().trim());
        if (dto.getMotivo() != null) dto.setMotivo(dto.getMotivo().trim());

        dto.setFechaModificacion(LocalDateTime.now());
        CitaRecord record = repository.actualizar(id, dto);
        return CitaMapper.toDTO(record);
    }

    @Override
    public boolean borrar(Integer id) {
        return repository.eliminar(id);
    }

    @Override
    public List<CitaDTO> porUsuario(Integer usuarioId) {
        return mostrarTodos().stream()
                .filter(c -> c.getUsuarioId() != null && c.getUsuarioId().equals(usuarioId))
                .collect(Collectors.toList());
    }

    @Override
    public List<CitaDTO> proximasPorUsuario(Integer usuarioId, int limit) {
        return mostrarTodos().stream()
                .filter(c -> c.getUsuarioId() != null && c.getUsuarioId().equals(usuarioId))
                .filter(c -> c.getFechaCita() != null && c.getFechaCita().isAfter(LocalDateTime.now().minusMinutes(1)))
                .sorted((a,b) -> a.getFechaCita().compareTo(b.getFechaCita()))
                .limit(limit)
                .collect(Collectors.toList());
    }

    @Override
    public List<CitaDTO> citasHoyPorUsuario(Integer usuarioId) {
        LocalDate today = LocalDate.now();
        return mostrarTodos().stream()
                .filter(c -> c.getUsuarioId() != null && c.getUsuarioId().equals(usuarioId))
                .filter(c -> c.getFechaCita() != null && c.getFechaCita().toLocalDate().equals(today))
                .sorted((a,b) -> a.getFechaCita().compareTo(b.getFechaCita()))
                .collect(Collectors.toList());
    }
}