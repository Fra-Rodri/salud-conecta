// package com.fran.saludconecta.export.controller;

// import java.security.Principal;

// import org.springframework.stereotype.Controller;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RequestParam;
// import org.springframework.web.servlet.mvc.support.RedirectAttributes;
// import org.springframework.ui.Model;

// @Controller
// @RequestMapping("/exports")
// public class ExportVistaController {

//     // @GetMapping("/excel")
//     // public String mostrarVistaExcel(Principal principal, Model model) {

//     // // Aquí obtén el nombre del usuario autenticado
//     // String usuarioActivo = principal.getName();
//     // model.addAttribute("usuarioActivo", usuarioActivo);

//     // return "exports/excel-export";
//     // }

//     // @GetMapping("/email")
//     // public String mostrarVistaEmail(Principal principal, Model model) {

//     // // Aquí obtén el nombre del usuario autenticado
//     // String usuarioActivo = principal.getName();
//     // model.addAttribute("usuarioActivo", usuarioActivo);

//     // return "exports/email-export";
//     // }
// }

package com.fran.saludconecta.export.controller;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controlador de vistas de exportación (Excel y envío por email).
 * - /exports/excel : muestra opciones de exportación a archivos
 * - /exports/email : formulario para enviar listados por correo
 * - POST /exports/pacientes/email : envía Excel de pacientes
 * - POST /exports/informes/email : envía Excel de informes
 */
@Controller
@RequestMapping("/exports")
public class ExportVistaController {

    // Inyecta aquí tus servicios reales de exportación/envío
    // @Autowired
    // private ExportService exportService;

    @GetMapping("/excel")
    public String mostrarVistaExcel(Principal principal, Model model) {
        if (principal != null) {
            model.addAttribute("usuarioActivo", principal.getName());
        } else {
            model.addAttribute("usuarioActivo", "Invitado");
        }
        return "exports/excel-export";
    }

    @GetMapping("/email")
    public String mostrarVistaEmail(Principal principal, Model model) {
        if (principal != null) {
            model.addAttribute("usuarioActivo", principal.getName());
        } else {
            model.addAttribute("usuarioActivo", "Invitado");
        }
        // Los mensajes (mensaje, tipoMensaje) llegan como flash attributes tras el
        // redirect
        return "exports/email-export";
    }

    @PostMapping("/pacientes/email")
    public String enviarPacientesEmail(Principal principal,
            @RequestParam("destinatario") String destinatario,
            RedirectAttributes redirectAttributes) {

        // Validación mínima
        if (destinatario == null || destinatario.isBlank()) {
            redirectAttributes.addFlashAttribute("mensaje", "El email destinatario es obligatorio");
            redirectAttributes.addFlashAttribute("tipoMensaje", "danger");
            return "redirect:/exports/email";
        }

        try {
            // TODO: Llamada real a servicio que genera y envía el Excel por correo
            // exportService.enviarPacientesExcel(destinatario);

            redirectAttributes.addFlashAttribute("mensaje", "Lista de pacientes enviada correctamente");
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("mensaje", "Error al enviar pacientes: " + ex.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "danger");
        }

        return "redirect:/exports/email";
    }

    @PostMapping("/informes/email")
    public String enviarInformesEmail(Principal principal,
            @RequestParam("destinatario") String destinatario,
            RedirectAttributes redirectAttributes) {

        if (destinatario == null || destinatario.isBlank()) {
            redirectAttributes.addFlashAttribute("mensaje", "El email destinatario es obligatorio");
            redirectAttributes.addFlashAttribute("tipoMensaje", "danger");
            return "redirect:/exports/email";
        }

        try {
            // TODO: Llamada real a servicio que genera y envía el Excel de informes
            // exportService.enviarInformesExcel(destinatario);

            redirectAttributes.addFlashAttribute("mensaje", "Lista de informes enviada correctamente");
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("mensaje", "Error al enviar informes: " + ex.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "danger");
        }

        return "redirect:/exports/email";
    }
}