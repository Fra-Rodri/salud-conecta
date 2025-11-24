package com.fran.saludconecta.export.service;

public interface IExportMailService {
    void enviarPacientesExcel(String destinatario) throws Exception;
}
