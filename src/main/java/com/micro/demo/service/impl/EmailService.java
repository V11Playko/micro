package com.micro.demo.service.impl;

import com.micro.demo.entities.Email;
import com.micro.demo.service.IEmailService;
import com.micro.demo.service.exceptions.MessageNotSendException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.thymeleaf.context.Context;

@Service
@Transactional
public class EmailService implements IEmailService {

    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;

    public EmailService(JavaMailSender javaMailSender, TemplateEngine templateEngine) {
        this.javaMailSender = javaMailSender;
        this.templateEngine = templateEngine;
    }

    @Override
    public void sendMail(Email email) throws MessagingException {
        try{
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(email.getDestinatario());
            helper.setSubject(email.getAsunto());

            // Procesar la plantilla Thymeleaf}

            Context context = new Context();
            context.setVariable("message", email.getMensaje());
            String contenidoHtml = templateEngine.process("email", context);

            helper.setText(contenidoHtml, true);

            javaMailSender.send(message);
        }catch (Exception e){
            throw new MessageNotSendException();
        }
    }
}
