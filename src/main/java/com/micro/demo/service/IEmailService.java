package com.micro.demo.service;

import com.micro.demo.entities.Email;
import jakarta.mail.MessagingException;

public interface IEmailService {

    void sendMail(Email email) throws MessagingException;
}
