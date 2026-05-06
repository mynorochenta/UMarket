package com.UMarket.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String emailRemitente;

    public void enviarEmailVerificacion(String emailDestino, String token) {

        String enlace = "http://localhost:8080/api/usuarios/verificar?token=" + token;

        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setFrom(emailRemitente);
        mensaje.setTo(emailDestino);
        mensaje.setSubject("Verifica tu cuenta en UMarket");
        mensaje.setText(
                "¡Hola! Bienvenido a UMarket.\n\n" +
                        "Para activar tu cuenta haz clic en el siguiente enlace:\n\n" +
                        enlace + "\n\n" +
                        "Este enlace expira en 24 horas.\n\n" +
                        "Si no creaste esta cuenta, ignora este mensaje."
        );

        mailSender.send(mensaje);
    }
}