package com.fran.saludconecta.controller;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fran.saludconecta.dto.ErrorResponse;
import com.fran.saludconecta.dto.PacienteDTO;
import com.fran.saludconecta.service.PacienteService;

import jakarta.servlet.http.HttpServletRequest;

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
	public List<PacienteDTO> listarTodos() {
		return pacienteService.listarTodos();
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<?> obtener(@PathVariable Integer id, HttpServletRequest request) { // @PathVariable extrae el valor id de la url
//		var paciente = pacienteService.obtenerPorId(id);
//		return paciente != null ? ResponseEntity.ok(paciente) : ResponseEntity.notFound().build();
		
		PacienteDTO pacienteEncontrado = pacienteService.obtenerPorId(id);
		
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
	public ResponseEntity<PacienteDTO> crear (@RequestBody PacienteDTO dto){ // @RequestBody convierte el JSON recibido en un objeto Java.
		var creado = pacienteService.crear(dto);
		return ResponseEntity.status(HttpStatus.CREATED).body(creado);
	}
	

	@PutMapping("/{id}")
	public ResponseEntity<PacienteDTO> actualizar(@PathVariable Integer id, @RequestBody PacienteDTO dto) {
		var actualizado = pacienteService.actualizar(id, dto);
		return actualizado != null ? ResponseEntity.ok(actualizado) : ResponseEntity.notFound().build();
	}
	

	@DeleteMapping("/{id}")
	public ResponseEntity<?> eliminar(@PathVariable Integer id, HttpServletRequest request) {
//		pacienteService.eliminar(id);
//		return ResponseEntity.noContent().build();
		
		boolean eliminado = pacienteService.eliminar(id);
		
		if (eliminado) {
			return ResponseEntity.noContent().build();
		} else {
//			Map<String, String> error = new HashMap<>();
//			error.put("error", "Paciente con ID " + id + " no encontrado");
//			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
			
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
