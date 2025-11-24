package com.fran.saludconecta.controller;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.ui.Model;

@Controller
@RequestMapping("/exports")
public class ExportsVistaController {

    @GetMapping("/excel")
    public String mostrarVistaExcel(Principal principal, Model model) {
        String usuario = principal.getName(); // Aquí obtén el nombre del usuario autenticado
        model.addAttribute("usuario", usuario);
        return "exports/excel-export";
    }

    @GetMapping("/email")
    public String mostrarVistaEmail(Principal principal, Model model) {
        String usuario = principal.getName(); // Aquí obtén el nombre del usuario autenticado
        model.addAttribute("usuario", usuario);
        return "exports/email-export";
    }
}