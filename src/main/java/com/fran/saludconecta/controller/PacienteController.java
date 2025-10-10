package com.fran.saludconecta.controller;

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

import com.fran.saludconecta.dto.PacienteDTO;
import com.fran.saludconecta.service.PacienteService;

/*
 * FALTA SABER:
 * 
 *  - @REST CONTROLLER
 *  - @REQUEST MAPPING
 *  - @GET MAPPING
 *  - @POST MAPPING
 *  - @PUT MAPPING
 *  - @DELETE MAPPING
 */

@RestController
@RequestMapping("/pacientes")
public class PacienteController {
	
	@Autowired
	private PacienteService pacienteService;
	
	@GetMapping
	public List<PacienteDTO> listarTodos() {
		return pacienteService.listarTodos();
	}
	
	/*
	 * AQUI ME FALTA SABER QUE ES :
	 * 
	 * - RESPONSE ENTITY.OK, 
	 * - @PATH VARIABLE,
	 * - RETURN CON TERNARIA
	 */
	@GetMapping("/{id}")
	public ResponseEntity<PacienteDTO> obtener(@PathVariable Integer id) {
		var paciente = pacienteService.obtenerPorId(id);
		return paciente != null ? ResponseEntity.ok(paciente) : ResponseEntity.notFound().build();
	}
	
	/*
	 * AQUI ME FALTA SABER QUE ES :
	 * 
	 * - RESPONSE ENTITY.STATUS, 
	 * - HTTP STATUS.CREATED,
	 */
	@PostMapping
	public ResponseEntity<PacienteDTO> crear (@RequestBody PacienteDTO dto){
		var creado = pacienteService.crear(dto);
		return ResponseEntity.status(HttpStatus.CREATED).body(creado);
	}
	
	/*
	 * AQUI ME FALTA SABER QUE ES :
	 * 
	 * - @PATH VARIABLE,
	 * - @REQUEST BODY
	 */
	@PutMapping("/{id}")
	public ResponseEntity<PacienteDTO> actualizar(@PathVariable Integer id, @RequestBody PacienteDTO dto) {
		var actualizado = pacienteService.actualizar(id, dto);
		return actualizado != null ? ResponseEntity.ok(actualizado) : ResponseEntity.notFound().build();
	}
	
	/*
	 * AQUI ME FALTA SABER QUE ES :
	 * 
	 * - @RESPONSE ENTITY.NO CONTENT
	 */
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
		pacienteService.eliminar(id);
		return ResponseEntity.noContent().build();
	}
	
}
