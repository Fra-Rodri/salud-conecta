package com.fran.saludconecta.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/exports")
public class ExportsVistaController {

    @GetMapping("/excel")
    public String mostrarVistaExcel() {
        return "exports/excel-export";
    }

    @GetMapping("/email")
    public String mostrarVistaEmail() {
        return "exports/email-export";
    }
}