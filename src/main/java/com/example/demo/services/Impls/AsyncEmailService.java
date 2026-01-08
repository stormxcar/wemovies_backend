package com.example.demo.services.Impls;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${spring.mail.username}")
    private String fromEmail;

    // @Value("${app.email.allowed-domains}")
    // private String allowedDomains;

    @Async
    public void sendEmailAsync(String to, String subject, String content) throws MailException {
        System.out.println("=== STARTING ASYNC EMAIL SEND ===");
        System.out.println("To: " + to);
        System.out.println("Subject: " + subject);
        System.out.println("From: " + fromEmail);
        System.out.println("Content length: " + content.length());

        // Validate email domain trong development mode
        // validateEmailDomain(to); // Tạm thời disable khi dùng Gmail

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true); // true = html content

            System.out.println("About to send email via mailSender...");
            mailSender.send(message);
            System.out.println("✅ Email sent successfully to: " + to);
        } catch (Exception e) {
            System.err.println("❌ Failed to send email to: " + to);
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            throw new MailException("Failed to send email", e) {};
        }
        System.out.println("=== ASYNC EMAIL SEND COMPLETED ===");
    }

    /**
     * Validate email domain - chỉ cho phép gửi đến các domain được cấu hình
     * Tạm thời disable khi dùng Gmail SMTP
     */
    /*
    private void validateEmailDomain(String email) {
        if (allowedDomains == null || allowedDomains.isEmpty()) {
            return; // Không validate nếu không cấu hình
        }

        String domain = email.substring(email.indexOf("@") + 1).toLowerCase();
        String[] allowed = allowedDomains.split(",");

        for (String allowedDomain : allowed) {
            if (domain.equals(allowedDomain.trim().toLowerCase())) {
                return; // Domain được phép
            }
        }

        throw new IllegalArgumentException("Email domain '" + domain + "' is not allowed in development mode. Allowed domains: " + allowedDomains);
    }
    */
}