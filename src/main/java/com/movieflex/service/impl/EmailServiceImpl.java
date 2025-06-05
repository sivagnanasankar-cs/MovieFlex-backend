package com.movieflex.service.impl;

import com.movieflex.config.DataSourceConfig;
import com.movieflex.dto.MailBody;
import com.movieflex.service.EmailService;
import com.movieflex.utils.CommonUtils;
import jakarta.mail.Address;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    private final DataSourceConfig config;

    @Autowired
    public EmailServiceImpl(JavaMailSender mailSender, DataSourceConfig config) {
        this.mailSender = mailSender;
        this.config = config;
    }

    public void sendSimpleMessage(MailBody mailBody) {
        String senderEmail = config.getDataSource().getSenderEmail();
        if(CommonUtils.checkIsNullOrEmpty(senderEmail))
            throw new RuntimeException("Email is blank");
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo(mailBody.getTo());
        simpleMailMessage.setFrom(senderEmail);
        simpleMailMessage.setSubject(mailBody.getSubject());
        simpleMailMessage.setText(mailBody.getText());
        mailSender.send(simpleMailMessage);
    }

}
