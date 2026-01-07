package com.example.demo.services.Impls;

import com.example.demo.services.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {
    @Autowired
    private JavaMailSender mailSender;

    @Override
    public void sendEmail(String to, String subject, String body) {
        System.out.println("Sending email to: " + to + " with subject: " + subject);
        MimeMessage message = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom("noreply@resend.dev"); // Hoặc domain của bạn
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true); // true để gửi HTML
            mailSender.send(message);
            System.out.println("Email sent successfully to: " + to);
        } catch (MessagingException e) {
            System.out.println("Email send failed: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Gửi email thất bại", e);
        }
    }

}