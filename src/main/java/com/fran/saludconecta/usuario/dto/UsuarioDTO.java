package com.fran.saludconecta.usuario.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.fran.saludconecta.jooq.enums.RolUsuario;
import com.fran.saludconecta.paciente.dto.PacienteDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
public class UsuarioDTO {
    private Integer id;

    @NotBlank(message = "El nombre no puede estar vacío")
    @Size(max = 50, message = "El nombre no puede tener más de 50 caracteres")
    private String nombre;

    private String email;

	@NotBlank(message = "El PASSWORD no puede estar vacío")
    @Size(max = 20, message = "El PASSWORD no puede tener más de 20 caracteres")
    private String password;
    
	private RolUsuario rolUsuario;
    private Integer negocioId;
    private LocalDateTime fechaCreacion;
	private LocalDateTime fechaModificacion;
    private List<PacienteDTO> pacientes;
}
