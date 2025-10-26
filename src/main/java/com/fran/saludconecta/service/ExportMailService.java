package com.fran.saludconecta.service;

public interface ExportMailService {
    void enviarPacientesExcel(String destinatario) throws Exception;
}
