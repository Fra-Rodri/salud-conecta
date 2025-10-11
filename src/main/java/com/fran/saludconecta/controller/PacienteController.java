package com.fran.saludconecta.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fran.saludconecta.dto.ErrorResponse;
import com.fran.saludconecta.dto.PacienteDTO;
import com.fran.saludconecta.service.PacienteService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

/*
 * Indican el tipo de petición HTTP
 *  - @GET MAPPING
 *  - @POST MAPPING
 *  - @PUT MAPPING
 *  - @DELETE MAPPING
 *  
 *  ResponseEntity.ok(...): devuelve respuesta HTTP 200 con cuerpo.
 *  ResponseEntity.status(HttpStatus.CREATED).body(...): devuelve 201 con el nuevo objeto.
 *  ResponseEntity.noContent().build(): devuelve 204 sin cuerpo (usado tras eliminar).
 */

@RestController // combina @Controller + @ResponseBody, por eso devuelve JSON directamente.
@RequestMapping("/pacientes") // prefijo común para todas las rutas.
public class PacienteController {
	
	@Autowired
	private PacienteService pacienteService;
	// [PacienteController] → usa → PacienteService → accede a datos → devuelve JSON
	
	@GetMapping
	public ResponseEntity<?> listarTodos(HttpServletRequest request) {
		List<PacienteDTO> listaPacientes = pacienteService.mostrarTodos();
		
		if (!listaPacientes.isEmpty()) {
			return ResponseEntity.ok(listaPacientes); // 200 OK con lista
		} else {
			ErrorResponse error = ErrorResponse.builder()
					.timeStamp(LocalDateTime.now())
					.status(HttpStatus.NOT_FOUND.value())
					.error("Not Found")
					.message("La cantidad de pacientes es: " + listaPacientes.size())
					.path(request.getRequestURI())
					.build();
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
		}
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<?> obtener(@PathVariable Integer id, HttpServletRequest request) { // @PathVariable extrae el valor id de la url
		PacienteDTO pacienteEncontrado = pacienteService.mostrarPorId(id);
		
		if (pacienteEncontrado != null) {
			return ResponseEntity.ok(pacienteEncontrado);
		} else {
			ErrorResponse error = ErrorResponse.builder()
					.timeStamp(LocalDateTime.now())
					.status(HttpStatus.NOT_FOUND.value())
					.error("Not Found")
					.message("Paciente con ID " + id + " no encontrado")
					.path(request.getRequestURI())
					.build();
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
		}
	}
	

	@PostMapping
	public ResponseEntity<?> crear (@Valid @RequestBody PacienteDTO dto, BindingResult result, HttpServletRequest request){ // @RequestBody convierte el JSON recibido en un objeto Java.
		if (result.hasErrors()) {
			String mensaje = result
					.getFieldErrors()
					.stream()
					.map(error -> error.getField() + ": " + error.getDefaultMessage())
					.reduce((m1, m2) -> m1 + "; " + m2)
					.orElse("Datos inválidos");
			
			ErrorResponse error = ErrorResponse.builder()
	                .timeStamp(LocalDateTime.now())
	                .status(HttpStatus.BAD_REQUEST.value())
	                .error("Validación fallida")
	                .message(mensaje.toString())
	                .path(request.getRequestURI())
	                .build();
			
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);

		}
		
		boolean pacienteCreado = pacienteService.crear(dto);
		
		if (pacienteCreado) {
			return ResponseEntity.status(HttpStatus.CREATED).body(dto);
		} else {
			ErrorResponse error = ErrorResponse.builder()
					.timeStamp(LocalDateTime.now())
					.status(HttpStatus.BAD_REQUEST.value())
					.error("Error al insertar")
					.message("Paciente con ID " + dto.getId() + " ya existe")
					.path(request.getRequestURI())
					.build();
			
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
		}		
	}
	

	@PutMapping("/{id}")
	public ResponseEntity<PacienteDTO> actualizar(@PathVariable Integer id, @RequestBody PacienteDTO dto) {
		PacienteDTO actualizarPaciente = pacienteService.modificar(id, dto);
		return actualizarPaciente != null ? ResponseEntity.ok(actualizarPaciente) : ResponseEntity.notFound().build();
	}
	

	@DeleteMapping("/{id}")
	public ResponseEntity<?> eliminar(@RequestParam Integer id, HttpServletRequest request) {
		boolean pacienteEliminado = pacienteService.borrar(id);
		
		if (pacienteEliminado) {
			return ResponseEntity.noContent().build();
		} else {
			ErrorResponse error = ErrorResponse.builder()
					.timeStamp(LocalDateTime.now())
					.status(HttpStatus.NOT_FOUND.value())
					.error("Not Found")
					.message("Paciente con ID " + id + " no encontrado")
					.path(request.getRequestURI())
					.build();
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
		}
	}
}
