package com.fran.saludconecta.negocio.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.fran.saludconecta.negocio.dto.NegocioDTO;
import com.fran.saludconecta.negocio.service.INegocioService;

import jakarta.validation.Valid;

import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;


@Controller
public class NegocioVistaController {

    @Autowired
    private INegocioService service;

    @GetMapping("/negocio-lista")
    public String mostrarLista(Principal principal, Model model) {

        // Aquí obtén el nombre del usuario autenticado
        String usuarioActivo = principal.getName(); 
        model.addAttribute("usuarioActivo", usuarioActivo);

        List<NegocioDTO> listaNegocios = service.mostrarTodos();
        model.addAttribute("negocios", listaNegocios);
        return "negocio/negocio-lista"; 
    }


    @GetMapping("/negocio/ver/{id}") 
    public String mostrarDetalle(Principal principal, @PathVariable Integer id, Model model) {
        
        // Aquí obtén el nombre del usuario autenticado
        String usuarioActivo = principal.getName(); 
        model.addAttribute("usuarioActivo", usuarioActivo);

    	NegocioDTO dto = service.mostrarPorId(id);
		model.addAttribute("negocio", dto); 
        return "negocio/negocio-detalle"; 
    }

    @GetMapping("/negocio/crear")
    public String mostrarFormularioCreacion(Principal principal, Model model) {
        
        // Aquí obtén el nombre del usuario autenticado
        String usuarioActivo = principal.getName(); 
        model.addAttribute("usuarioActivo", usuarioActivo);

        model.addAttribute("negocio", new NegocioDTO());
        return "negocio/negocio-crear";
    }

    @PostMapping("/negocio/crear")
    public String procesarCreacion(Principal principal, @Valid @ModelAttribute("negocio") NegocioDTO dto, BindingResult result, Model model) {
        
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
            // Asumiendo que la comprobación está basada en nombre:
            result.rejectValue("nombre", "error.nombre", "Ya existe un negocio con ese nombre");
            return "negocio/negocio-crear";
        }

        service.crear(dto);
        return "redirect:/negocio-lista";
    }

    @GetMapping("/negocio/editar/{id}")
    public String mostrarFormularioEdicion(Principal principal, @PathVariable Integer id, Model model) {
        
        // Aquí obtén el nombre del usuario autenticado
        String usuarioActivo = principal.getName(); 
        model.addAttribute("usuarioActivo", usuarioActivo);

        NegocioDTO dto = service.mostrarPorId(id);
        if (dto != null) {
            model.addAttribute("negocio", dto);
            return "negocio/negocio-editar";

        } else {
            return "redirect:/negocio-lista";
        }
    }
    
    @PostMapping("/negocio/editar/{id}")
    public String procesarEdicion(Principal principal, @PathVariable Integer id, @Valid @ModelAttribute("negocio") NegocioDTO dto, BindingResult result, Model model) {
        
        // Aquí obtén el nombre del usuario autenticado
        String usuarioActivo = principal.getName(); 
        model.addAttribute("usuarioActivo", usuarioActivo);

        // Si hay errores de validación estándar (NotBlank, Size, ...)
        if (result.hasErrors()) {
            return "usuario/usuario-editar"; // vuelve a mostrar el formulario con errores
        }

        // Comprobar existencia: suponiendo que el servicio tiene un método para ello.
        // Si no existe, puedes usar service.mostrarTodos().stream().anyMatch(...)
        boolean creado = service.comprobarCrear(dto); // según tu impl. actual devuelve boolean
        if (!creado) {
            // Asumiendo que la comprobación está basada en nombre:
            result.rejectValue("nombre", "error.nombre", "Ya existe un negocio con ese nombre");
            return "negocio/negocio-editar";
        }

        service.modificar(id, dto);
        return "redirect:/negocio-lista";
    }
    
    
    @GetMapping("/negocio/eliminar/{id}")
    public String mostrarConfirmacionEliminacion(Principal principal, @PathVariable Integer id, Model model) {
        
        // Aquí obtén el nombre del usuario autenticado
        String usuarioActivo = principal.getName(); 
        model.addAttribute("usuarioActivo", usuarioActivo);
        
        NegocioDTO dto = service.mostrarPorId(id);
        if (dto != null) {
            model.addAttribute("negocio", dto);
            return "negocio/negocio-eliminar-confirmar";

        } else {
            return "redirect:/negocio-lista";
        }
    }

    @PostMapping("/negocio/eliminar/{id}")
    public String procesarEliminacion(Principal principal, @PathVariable Integer id, Model model) {
        
        // Aquí obtén el nombre del usuario autenticado
        String usuarioActivo = principal.getName(); 
        model.addAttribute("usuarioActivo", usuarioActivo);

        service.borrar(id);
        return "redirect:/negocio-lista";
    }
}
