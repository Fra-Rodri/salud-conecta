package com.fran.saludconecta.usuario.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.fran.saludconecta.usuario.dto.UsuarioDTO;
import com.fran.saludconecta.usuario.service.IUsuarioService;

@Controller
public class UsuarioVistaController {

    @Autowired
    private IUsuarioService usuarioService;

    @GetMapping("/usuarios-lista")
    public String mostrarListaUsuarios(Principal principal, Model model) {
        String usuario = principal.getName(); // Aquí obtén el nombre del usuario autenticado
        model.addAttribute("usuario", usuario);
        List<UsuarioDTO> listaUsuarios = usuarioService.mostrarTodos();
        model.addAttribute("usuarios", listaUsuarios);
        return "usuarios/usuarios-lista";
    }


}
