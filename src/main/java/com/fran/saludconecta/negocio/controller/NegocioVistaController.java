package com.fran.saludconecta.negocio.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.fran.saludconecta.negocio.dto.NegocioDTO;
import com.fran.saludconecta.negocio.service.INegocioService;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class NegocioVistaController {

    @Autowired
    private INegocioService negocioService;

    @GetMapping("/negocio-lista")
    public String mostrarListaNegocios(Principal principal, Model model) {

        // Aquí obtén el nombre del usuario autenticado
        String usuarioActivo = principal.getName(); 
        model.addAttribute("usuarioActivo", usuarioActivo);

        List<NegocioDTO> listaNegocios = negocioService.mostrarTodos();
        model.addAttribute("negocios", listaNegocios);
        return "negocio/negocio-lista"; 
    }
}
