package com.fran.saludconecta.export.controller;

import com.fran.saludconecta.dto.PacienteDTO;
import com.fran.saludconecta.export.service.IExportMailService;
import com.fran.saludconecta.service.PacienteService;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.time.LocalDate;
import java.time.Period;

@Controller
@RequestMapping("/export")
public class ExportController {

    @Autowired
    private PacienteService pacienteService;

    @Autowired
    private IExportMailService exportMailService;
    @PostMapping("/pacientes/email")
    public String enviarPacientesPorEmail(@RequestParam String destinatario, RedirectAttributes redirectAttributes) {
        try {
            exportMailService.enviarPacientesExcel(destinatario);
            redirectAttributes.addFlashAttribute("mensaje", "Excel enviado correctamente a " + destinatario);
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje", "Error al enviar el excel: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "danger");
        }
        return "redirect:/exports/email";
    }

    @PostMapping("/informes/email")
    public String enviarInformesPorEmail(@RequestParam String destinatario, RedirectAttributes redirectAttributes) {
        try {
            // TODO: Implementar envío de informes
            redirectAttributes.addFlashAttribute("mensaje", "Función no implementada aún");
            redirectAttributes.addFlashAttribute("tipoMensaje", "warning");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje", "Error al enviar el excel: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "danger");
        }
        return "redirect:/exports/email";
    }

    @GetMapping("/pacientes")
    public ResponseEntity<ByteArrayResource> exportarPacientesExcel() {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Pacientes");

            // Estilo para la cabecera
            CellStyle headerStyle = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setBold(true);
            headerStyle.setFont(font);
            headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            Row header = sheet.createRow(0);
            String[] columnas = {"ID", "Nombre", "Apellidos", "DNI", "Teléfono", "Email", "Fecha Nacimiento", "Edad"};
            for (int i = 0; i < columnas.length; i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(columnas[i]);
                cell.setCellStyle(headerStyle);
                sheet.setColumnWidth(i, 20 * 256); // ancho de columna
            }

            List<PacienteDTO> pacientes = pacienteService.mostrarTodos();
            int rowIdx = 1;
            for (PacienteDTO p : pacientes) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(p.getId());
                row.createCell(1).setCellValue(p.getNombre());
                row.createCell(2).setCellValue(p.getNombre());
                row.createCell(3).setCellValue(p.getDni());
                row.createCell(4).setCellValue(p.getDni());
                row.createCell(5).setCellValue(p.getDni());
                row.createCell(6).setCellValue(p.getFechaNacimiento() != null ? p.getFechaNacimiento().toString() : "");
                // Calcular edad si hay fecha de nacimiento
                if (p.getFechaNacimiento() != null) {
                    int edad = Period.between(p.getFechaNacimiento(), LocalDate.now()).getYears();
                    row.createCell(7).setCellValue(edad);
                } else {
                    row.createCell(7).setCellValue("");
                }
            }
            workbook.write(out);
            ByteArrayResource resource = new ByteArrayResource(out.toByteArray());
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=pacientes.xlsx")
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .contentLength(resource.contentLength())
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/informes")
    public ResponseEntity<String> exportarInformesExcel() {
        // TODO: Implementar exportación de informes
        return ResponseEntity.ok("Función no implementada aún");
    }
}
