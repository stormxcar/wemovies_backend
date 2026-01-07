package com.example.demo.services.Impls;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;

@Service
public class AsyncEmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Async
    public void sendEmailAsync(String to, String subject, String content) throws MailException {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom("noreply@wemovies.com");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true); // true = html content

            mailSender.send(message);
            System.out.println("Email sent successfully to: " + to);
        } catch (Exception e) {
            System.err.println("Failed to send email to: " + to + ", Error: " + e.getMessage());
            throw new MailException("Failed to send email", e) {};
        }
    }
}