package com.movieflex.service;

import com.movieflex.dto.MailBody;

public interface EmailService {

    void sendSimpleMessage(MailBody mailBody);

}
