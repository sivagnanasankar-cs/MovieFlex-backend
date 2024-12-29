package com.movieflex.service.impl;

import com.movieflex.config.DataSourceConfig;
import com.movieflex.dto.MailBody;
import com.movieflex.service.EmailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    private final String senderEmail;

    public EmailServiceImpl(JavaMailSender mailSender, DataSourceConfig config) {
        this.mailSender = mailSender;
        this.senderEmail = config.getDataSource().getSenderEmail();
    }

    public void sendSimpleMessage(MailBody mailBody) {
        if(senderEmail.isBlank()){
            throw new RuntimeException("Email is blank");
        }
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo(mailBody.getTo());
        simpleMailMessage.setFrom(senderEmail);
        simpleMailMessage.setSubject(mailBody.getSubject());
        simpleMailMessage.setText(mailBody.getText());
        mailSender.send(simpleMailMessage);
    }

}

