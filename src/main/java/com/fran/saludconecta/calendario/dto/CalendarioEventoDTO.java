package com.fran.saludconecta.calendario.dto;

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
public class CalendarioEventoDTO {
    private LocalDate fecha;
    private String titulo;
    private String descripcion;
}
