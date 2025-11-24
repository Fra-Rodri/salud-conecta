package com.fran.saludconecta.usuario.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.fran.saludconecta.paciente.dto.PacienteDTO;
import com.fran.saludconecta.usuario.dto.UsuarioDTO;
import com.fran.saludconecta.usuario.service.IUsuarioService;

import jakarta.validation.Valid;

@Controller
public class UsuarioVistaController {

    @Autowired
    private IUsuarioService service;

    @GetMapping("/usuario-perfil/{id}")
    public String mostrarPerfil(Principal principal, @PathVariable Integer id, Model model) {
        // Aquí obtén el nombre del usuario autenticado
        String usuarioActivo = principal.getName(); 
        model.addAttribute("usuarioActivo", usuarioActivo);

    	UsuarioDTO dto = service.mostrarDetallesPorId(id);
		model.addAttribute("usuario", dto); 
        return "usuario/usuario-perfil";
    }

    @GetMapping("/usuario-lista")
    public String mostrarLista(Principal principal, Model model) {
        
        // Aquí obtén el nombre del usuario autenticado
        String usuarioActivo = principal.getName(); 
        model.addAttribute("usuario", usuarioActivo);

        List<UsuarioDTO> lista = service.mostrarTodos();
        model.addAttribute("usuarios", lista);
        return "usuario/usuario-lista";
    }

    @GetMapping("/usuario/ver/{id}") 
    public String mostrarDetalle(Principal principal, @PathVariable Integer id, Model model) {
        
        // Aquí obtén el nombre del usuario autenticado
        String usuarioActivo = principal.getName(); 
        model.addAttribute("usuario", usuarioActivo);

    	UsuarioDTO dto = service.mostrarDetallesPorId(id);
		model.addAttribute("usuario", dto); 
        return "usuario/usuario-detalle"; 
    }

    @GetMapping("/usuario/crear")
    public String mostrarFormularioCreacion(Principal principal, Model model) {
        
        // Aquí obtén el nombre del usuario autenticado
        String usuarioActivo = principal.getName(); 
        model.addAttribute("usuario", usuarioActivo);

        model.addAttribute("usuario", new UsuarioDTO());
        return "usuario/usuario-crear";
    }

    @PostMapping("/usuario/crear")
    public String procesarCreacion(Principal principal, @Valid @ModelAttribute("usuario") UsuarioDTO dto, BindingResult result, Model model) {
        
        // Aquí obtén el nombre del usuario autenticado
        String usuarioActivo = principal.getName(); 
        model.addAttribute("usuarioActivo", usuarioActivo);

        // Si hay errores de validación estándar (NotBlank, Size, ...)
        if (result.hasErrors()) {
            return "usuario/usuario-crear"; // vuelve a mostrar el formulario con errores
        }

        // Comprobar existencia: suponiendo que el servicio tiene un método para ello.
        // Si no existe, puedes usar service.mostrarTodos().stream().anyMatch(...)
        boolean creado = service.crear(dto); // según tu impl. actual devuelve boolean
        if (!creado) {
            // Asumiendo que la comprobación está basada en email:
            result.rejectValue("email", "error.email", "Ya existe un usuario con ese email");
            return "usuario/usuario-crear";
        }

        service.crear(dto);
        return "redirect:/usuario-lista";
    }

    @GetMapping("/usuario/editar/{id}")
    public String mostrarFormularioEdicion(Principal principal, @PathVariable Integer id, Model model) {
        
        // Aquí obtén el nombre del usuario autenticado
        String usuarioActivo = principal.getName(); 
        model.addAttribute("usuarioActivo", usuarioActivo);

        UsuarioDTO dto = service.mostrarPorId(id);
        if (dto != null) {
            model.addAttribute("usuario", dto);
            return "usuario/usuario-editar";
        } else {
            return "redirect:/usuario-lista";
        }
    }
    
    @PostMapping("/usuario/editar/{id}")
    public String procesarEdicion(Principal principal, @PathVariable Integer id, @Valid @ModelAttribute("usuario") UsuarioDTO dto, BindingResult result, Model model) {
        
        // Aquí obtén el nombre del usuario autenticado
        String usuarioActivo = principal.getName(); 
        model.addAttribute("usuarioActivo", usuarioActivo);

        // Si hay errores de validación estándar (NotBlank, Size, ...)
        if (result.hasErrors()) {
            return "usuario/usuario-editar"; // vuelve a mostrar el formulario con errores
        }

        // Comprobar existencia: suponiendo que el servicio tiene un método para ello.
        // Si no existe, puedes usar service.mostrarTodos().stream().anyMatch(...)
        boolean creado = service.crear(dto); // según tu impl. actual devuelve boolean
        if (!creado) {
            // Asumiendo que la comprobación está basada en email:
            result.rejectValue("email", "error.email", "Ya existe un usuario con ese email");
            return "usuario/usuario-editar";
        }

        service.modificar(id, dto);
        return "redirect:/usuario-lista";
    }
    
    
    @GetMapping("/usuario/eliminar/{id}")
    public String mostrarConfirmacionEliminacion(Principal principal, @PathVariable Integer id, Model model) {
        
        // Aquí obtén el nombre del usuario autenticado
        String usuarioActivo = principal.getName(); 
        model.addAttribute("usuario", usuarioActivo);
        
        UsuarioDTO dto = service.mostrarPorId(id);
        if (dto != null) {
            model.addAttribute("usuario", dto);
            return "usuario/usuario-eliminar-confirmar";
        } else {
            return "redirect:/usuario-lista";
        }
    }

    @PostMapping("/usuario/eliminar/{id}")
    public String procesarEliminacion(Principal principal, @PathVariable Integer id, Model model) {
        
        // Aquí obtén el nombre del usuario autenticado
        String usuarioActivo = principal.getName(); 
        model.addAttribute("usuario", usuarioActivo);

        service.borrar(id);
        return "redirect:/usuario-lista";
    }
}
