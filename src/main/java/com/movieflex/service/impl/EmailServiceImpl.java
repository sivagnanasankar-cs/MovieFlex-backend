package com.movieflex.service.impl;

import com.movieflex.dto.MailBody;
import com.movieflex.service.EmailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${sender.email}")
    private String fromEmail;

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendSimpleMessage(MailBody mailBody) {
        if(fromEmail.isBlank()){
            throw new RuntimeException("Email is blank");
        }
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo(mailBody.getTo());
        simpleMailMessage.setFrom(fromEmail);
        simpleMailMessage.setSubject(mailBody.getSubject());
        simpleMailMessage.setText(mailBody.getText());
        mailSender.send(simpleMailMessage);
    }

}

