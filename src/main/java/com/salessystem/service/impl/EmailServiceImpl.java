package com.salessystem.service.impl;

import com.salessystem.service.EmailService;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {
    @Override
    public void sendEmail(String to, String subject, String body) {
        // Implementación mínima: loguear en stdout. El proyecto puede inyectar una implementación real si desea.
        System.out.println("Enviando email a: " + to);
        System.out.println("Asunto: " + subject);
        System.out.println("Cuerpo:\n" + body);
    }
}

