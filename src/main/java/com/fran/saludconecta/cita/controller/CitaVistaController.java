package com.fran.saludconecta.cita.controller;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fran.saludconecta.cita.dto.CitaDTO;
import com.fran.saludconecta.cita.service.ICitaService;
import com.fran.saludconecta.jooq.enums.EstadoCita;
import com.fran.saludconecta.paciente.service.IPacienteService;
import com.fran.saludconecta.usuario.service.IUsuarioService;

import jakarta.validation.Valid;

@Controller
public class CitaVistaController {

    @Autowired
    private ICitaService citaService;

    @Autowired
    private IPacienteService pacienteService;

    @Autowired
    private IUsuarioService usuarioService;

    /**
     * Muestra el formulario para crear una nueva cita.
     */
    @GetMapping("/cita/crear")
    public String mostrarFormularioCrear(Principal principal, Model model) {
        String usuarioActivo = principal != null ? principal.getName() : "Invitado";
        model.addAttribute("usuarioActivo", usuarioActivo);

        // Crear un DTO vacío para el formulario
        CitaDTO cita = new CitaDTO();
        // Estado por defecto: PENDIENTE
        cita.setEstado(EstadoCita.pendiente);

        model.addAttribute("cita", cita);
        model.addAttribute("pacientes", pacienteService.mostrarTodos());
        model.addAttribute("usuarios", usuarioService.mostrarTodos());
        model.addAttribute("estados", EstadoCita.values());

        return "cita/cita-crear";
    }

    /**
     * Procesa el formulario de creación de cita.
     */
    /**
     * Procesa el formulario de creación de cita.
     */
    @PostMapping("/cita/crear")
    public String crearCita(
            @ModelAttribute("cita") CitaDTO cita,
            BindingResult result,
            Principal principal,
            Model model,
            RedirectAttributes redirectAttributes) {

        // Validaciones manuales
        if (cita.getPacienteId() == null) {
            result.rejectValue("pacienteId", "error.cita", "Debe seleccionar un paciente");
        }

        if (cita.getUsuarioId() == null) {
            result.rejectValue("usuarioId", "error.cita", "Debe seleccionar un profesional");
        }

        if (cita.getFechaCita() == null) {
            result.rejectValue("fechaCita", "error.cita", "La fecha de la cita es obligatoria");
        } else if (cita.getFechaCita().isBefore(LocalDateTime.now())) {
            result.rejectValue("fechaCita", "error.cita", "La fecha de la cita debe ser en el futuro");
        }

        if (cita.getMotivo() != null && cita.getMotivo().length() > 500) {
            result.rejectValue("motivo", "error.cita", "El motivo no puede superar los 500 caracteres");
        }

        // DEBUG: mostrar todos los errores
        System.out.println("=== DEBUG CREAR CITA ===");
        System.out.println("Has errors: " + result.hasErrors());
        System.out.println("CitaDTO recibido: " + cita);
        System.out.println("  - pacienteId: " + cita.getPacienteId());
        System.out.println("  - usuarioId: " + cita.getUsuarioId());
        System.out.println("  - fechaCita: " + cita.getFechaCita());
        System.out.println("  - motivo: " + cita.getMotivo());
        System.out.println("  - estado: " + cita.getEstado());
        if (result.hasErrors()) {
            System.out.println("Errores encontrados:");
            result.getAllErrors().forEach(error -> {
                System.out.println("  - " + error);
            });
        }
        System.out.println("========================");

        // Si hay errores, volver al formulario
        if (result.hasErrors()) {
            String usuarioActivo = principal != null ? principal.getName() : "Invitado";
            model.addAttribute("usuarioActivo", usuarioActivo);
            model.addAttribute("pacientes", pacienteService.mostrarTodos());
            model.addAttribute("usuarios", usuarioService.mostrarTodos());
            model.addAttribute("estados", EstadoCita.values());
            return "cita/cita-crear";
        }

        // Poblar nombres desde los IDs
        var paciente = pacienteService.mostrarPorId(cita.getPacienteId());
        if (paciente != null) {
            cita.setNombrePaciente(paciente.getNombre());
        }

        var usuario = usuarioService.mostrarPorId(cita.getUsuarioId());
        if (usuario != null) {
            cita.setNombreUsuario(usuario.getNombre());
        }

        // Establecer fechas
        LocalDateTime now = LocalDateTime.now();
        cita.setFechaCreacion(now);
        cita.setFechaModificacion(now);

        // Crear la cita
        boolean creado = citaService.crear(cita);

        if (creado) {
            redirectAttributes.addFlashAttribute("mensaje", "Cita creada exitosamente");
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");
        } else {
            redirectAttributes.addFlashAttribute("mensaje", "Error al crear la cita");
            redirectAttributes.addFlashAttribute("tipoMensaje", "danger");
        }

        return "redirect:/calendario-vista";
    }

    /**
     * Muestra el formulario de búsqueda y listado de citas para eliminar.
     */
    @GetMapping("/cita/buscar")
    public String buscarCitas(
            @RequestParam(required = false) Integer pacienteId,
            @RequestParam(required = false) Integer usuarioId,
            @RequestParam(required = false) String fecha,
            Principal principal,
            Model model) {

        String usuarioActivo = principal != null ? principal.getName() : "Invitado";
        model.addAttribute("usuarioActivo", usuarioActivo);

        // Cargar listas para los filtros
        model.addAttribute("pacientes", pacienteService.mostrarTodos());
        model.addAttribute("usuarios", usuarioService.mostrarTodos());

        // Mantener los valores de los filtros en el formulario
        model.addAttribute("pacienteId", pacienteId);
        model.addAttribute("usuarioId", usuarioId);
        model.addAttribute("fecha", fecha);

        // Si hay al menos un filtro, buscar citas
        if (pacienteId != null || usuarioId != null || fecha != null) {
            List<CitaDTO> todasLasCitas = citaService.mostrarTodos();
            List<CitaDTO> citasFiltradas = todasLasCitas.stream()
                    .filter(c -> pacienteId == null || c.getPacienteId().equals(pacienteId))
                    .filter(c -> usuarioId == null || c.getUsuarioId().equals(usuarioId))
                    .filter(c -> {
                        if (fecha == null || fecha.isEmpty())
                            return true;
                        if (c.getFechaCita() == null)
                            return false;
                        return c.getFechaCita().toLocalDate().toString().equals(fecha);
                    })
                    .sorted((a, b) -> a.getFechaCita().compareTo(b.getFechaCita()))
                    .toList();

            model.addAttribute("citas", citasFiltradas);
        }

        return "cita/cita-buscar";
    }

    /**
     * Muestra la confirmación para eliminar una cita.
     */
    @GetMapping("/cita/eliminar/{id}")
    public String confirmarEliminar(@PathVariable Integer id, Principal principal, Model model) {
        String usuarioActivo = principal != null ? principal.getName() : "Invitado";
        model.addAttribute("usuarioActivo", usuarioActivo);
        CitaDTO cita = citaService.mostrarPorId(id);

        // Poblar los nombres desde los IDs
        if (cita.getPacienteId() != null) {
            var paciente = pacienteService.mostrarPorId(cita.getPacienteId());
            if (paciente != null) {
                cita.setNombrePaciente(paciente.getNombre());
            }
        }

        if (cita.getUsuarioId() != null) {
            var usuario = usuarioService.mostrarPorId(cita.getUsuarioId());
            if (usuario != null) {
                cita.setNombreUsuario(usuario.getNombre());
            }
        }

        model.addAttribute("cita", cita);

        return "cita/cita-eliminar-confirmar";
    }

    /**
     * Elimina la cita.
     */
    @PostMapping("/cita/eliminar/{id}")
    public String eliminarCita(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        boolean eliminado = citaService.borrar(id);

        if (eliminado) {
            redirectAttributes.addFlashAttribute("mensaje", "Cita eliminada exitosamente");
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");
        } else {
            redirectAttributes.addFlashAttribute("mensaje", "Error al eliminar la cita");
            redirectAttributes.addFlashAttribute("tipoMensaje", "danger");
        }

        return "redirect:/cita/buscar";
    }
}
