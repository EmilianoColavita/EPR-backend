package com.epr.backend.service;

public interface EmailService {
    void enviarConfirmacionSolicitudEvaluacion(String email, String nombreCompleto);

    void enviarCuentaActivada(String email, String nombre);

    void enviarRestablecerContrasena(String email, String nombre, String link, long minutosValidez);
}
