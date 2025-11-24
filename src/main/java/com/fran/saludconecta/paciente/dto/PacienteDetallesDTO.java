package com.fran.saludconecta.paciente.dto;

import java.time.LocalDate;
import java.util.List;

import com.fran.saludconecta.informe.dto.InformeDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PacienteDetallesDTO extends PacienteDTO {
	private Boolean alta;
	private LocalDate fechaCreacion;
	private LocalDate fechaModificacion;
	private List<InformeDTO> informe;
//	private Cita 

}
