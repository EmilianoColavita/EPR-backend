package com.epr.backend.service.impl;

import com.epr.backend.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${epr.mail.from}")
    private String from;

    @Async
    @Override
    public void enviarConfirmacionSolicitudEvaluacion(String email, String nombreCompleto) {
        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setFrom(from);
        mensaje.setTo(email);
        mensaje.setSubject("Recibimos tu solicitud de evaluación");
        mensaje.setText("""
                Hola %s,

                Recibimos tu solicitud de evaluación. Pronto nos pondremos en contacto para coordinar tu evaluación.

                Equipo E.P.R
                """.formatted(nombreCompleto));

        try {
            mailSender.send(mensaje);
        } catch (MailException e) {
            log.error("No se pudo enviar el email de confirmación de solicitud a {}", email, e);
        }
    }

    @Async
    @Override
    public void enviarCuentaActivada(String email, String nombre) {
        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setFrom(from);
        mensaje.setTo(email);
        mensaje.setSubject("Tu cuenta en E.P.R fue activada");
        mensaje.setText("""
                Hola %s,

                Tu cuenta ya está activa. Ya podés iniciar sesión y acceder a tu panel.

                Equipo E.P.R
                """.formatted(nombre));

        try {
            mailSender.send(mensaje);
        } catch (MailException e) {
            log.error("No se pudo enviar el email de activación de cuenta a {}", email, e);
        }
    }

    @Async
    @Override
    public void enviarRestablecerContrasena(String email, String nombre, String link, long minutosValidez) {
        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setFrom(from);
        mensaje.setTo(email);
        mensaje.setSubject("Restablecé tu contraseña de E.P.R");
        mensaje.setText("""
                Hola %s,

                Recibimos un pedido para restablecer tu contraseña. Ingresá al siguiente link para elegir una nueva (es válido por %d minutos y se puede usar una sola vez):

                %s

                Si no fuiste vos, ignorá este mensaje: tu contraseña actual no se modifica.

                Equipo E.P.R
                """.formatted(nombre, minutosValidez, link));

        try {
            mailSender.send(mensaje);
        } catch (MailException e) {
            log.error("No se pudo enviar el email de restablecimiento de contraseña a {}", email, e);
        }
    }
}
