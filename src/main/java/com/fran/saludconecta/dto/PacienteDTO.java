package com.fran.saludconecta.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PacienteDTO {
	private Integer id;
	private String nombre;
	private String dni;
	private LocalDate fechaNacimiento;
}
