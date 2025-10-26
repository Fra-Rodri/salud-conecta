package com.fran.saludconecta.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
import com.fran.saludconecta.dto.PacienteDetallesDTO;
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
 *  
 *  @RequestBody convierte el JSON recibido en un objeto Java y •  
 *  @Valid: activa las validaciones que definiste en PacienteDTO (como @NotBlank, @Size, etc.).  
 *  BindingResult result: captura los errores de validación si los hay.
 *  
 *  En Spring MVC, BindingResult debe ir inmediatamente después del parámetro 
 *  anotado con @Valid. Si no lo haces así, Spring ignora el BindingResult y 
 *  lanza una excepción directamente (lo que rompe tu lógica personalizada de errores).
 *  
 *  @PathVariable extrae el valor id de la url como en UPDATE
 *  @PathVariable: viene en la ruta como en DELETE
 *  
 */

@RestController // combina @Controller + @ResponseBody, por eso devuelve JSON directamente.
@RequestMapping("/api/pacientes") // prefijo común para todas las rutas.
public class PacienteController {
	
	@Autowired
	private PacienteService service; // [PacienteController] → usa → PacienteService → accede a datos → devuelve JSON
	
	@GetMapping
	public ResponseEntity<?> listarTodos(HttpServletRequest request) {
		List<PacienteDTO> listaPacientes = service.mostrarTodos();
		
		if (!listaPacientes.isEmpty()) {
			return ResponseEntity.ok(listaPacientes); // 200 OK con lista
		} else {
			ErrorResponse error = mostrarError(request, HttpStatus.OK, "La lista de pacientes está vacía");
			return ResponseEntity.status(HttpStatus.OK).body(error); // Aquí lo devuelve en la propia llamada al lado de los segundos tardados
		}
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<?> obtener(@PathVariable Integer id, HttpServletRequest request) { 
		PacienteDTO pacienteEncontrado = service.mostrarPorId(id);
		
		if (pacienteEncontrado != null) {
			return ResponseEntity.ok(pacienteEncontrado);
		} else {
			ErrorResponse error = mostrarError(request, HttpStatus.OK, "Paciente con ID " + id + " no encontrado");
			return ResponseEntity.status(HttpStatus.OK).body(error);
		}
	}
	

	@PostMapping
	public ResponseEntity<?> crear (@Valid @RequestBody PacienteDTO dto, BindingResult result, HttpServletRequest request){ 
		
		if (result.hasErrors()) {
			
			String mensaje = result.getFieldErrors().stream().map(error -> error.getField() + ": " + error.getDefaultMessage()).collect(Collectors.joining(" | "));
			ErrorResponse error = mostrarError(request, HttpStatus.BAD_REQUEST, mensaje);

			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
		
		} else {
			
			service.crear(dto);
			return ResponseEntity.status(HttpStatus.CREATED).body(dto);
		}		
	}
	

	@PutMapping("/{id}")
	public ResponseEntity<?> actualizar(@Valid  @RequestBody PacienteDTO dto, BindingResult result, @PathVariable Integer id, HttpServletRequest request) {
		
		if (result.hasErrors()) {
			String mensaje = result.getFieldErrors().stream().map(error -> error.getField() + ": " + error.getDefaultMessage()).collect(Collectors.joining(" | "));
			ErrorResponse error = mostrarError(request, HttpStatus.BAD_REQUEST, mensaje);
			
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
		} else {
			Boolean existe = service.mostrarTodos().stream().anyMatch(p -> p.getId().equals(id));
			
			if (!existe) {
				ErrorResponse error = mostrarError(request, HttpStatus.BAD_REQUEST, "Paciente con ID " + id + " no encontrado");
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
			} else {
				PacienteDTO actualizarPaciente = service.modificar(id, dto);
				return ResponseEntity.ok(actualizarPaciente);
			}
		}
	}
	
	@GetMapping("detalles/{id}")
	public ResponseEntity<?> detallesPaciente(@PathVariable Integer id, HttpServletRequest request) { 
		PacienteDTO detallesDTO = service.mostrarDetallesPorId(id);
		
		if (detallesDTO != null) {
			return ResponseEntity.ok(detallesDTO);
		} else {
			ErrorResponse error = mostrarError(request, HttpStatus.OK, "ID " + id + " no encontrado");
			return ResponseEntity.status(HttpStatus.OK).body(error);
		}
	}
	
	private ErrorResponse mostrarError(HttpServletRequest request, HttpStatus status , String message) {
		ErrorResponse error = ErrorResponse.builder() // Esto es el response personalizado
				.timeStamp(LocalDateTime.now())
				.status(status.value())
				.error("Algo ha ido mal")
				.message(message)
				.path(request.getRequestURI())
				.build();
		
		return error;
	}

	@DeleteMapping("/eliminar")
	public ResponseEntity<?> eliminar(@RequestParam Integer id, HttpServletRequest request) {
		boolean pacienteEliminado = service.borrar(id);
		
		if (pacienteEliminado) {
			return ResponseEntity.status(HttpStatus.OK).body("Paciente " + id + " eliminado"); 
		} else {

			ErrorResponse error = mostrarError(request, HttpStatus.BAD_REQUEST, "Paciente con ID " + id + " no encontrado");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
		}
	}
}
