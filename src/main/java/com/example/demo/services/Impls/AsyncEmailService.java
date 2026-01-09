package com.example.demo.services.Impls;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;

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
        } catch (MessagingException e) {
            System.err.println("❌ MessagingException - Email sending failed to: " + to);
            System.err.println("Error: " + e.getMessage());
            if (e.getCause() != null) {
                System.err.println("Cause: " + e.getCause().getMessage());
                // Check for common issues
                String causeMsg = e.getCause().getMessage().toLowerCase();
                if (causeMsg.contains("authentication failed") || causeMsg.contains("535")) {
                    System.err.println("💡 Authentication failed - Check username/password");
                    System.err.println("💡 For Gmail: Use App Password instead of regular password");
                    System.err.println("💡 Create App Password at: https://myaccount.google.com/apppasswords");
                } else if (causeMsg.contains("unsupported or unrecognized ssl message") || causeMsg.contains("sslexception")) {
                    System.err.println("💡 SSL protocol mismatch - Gmail requires different SSL configuration");
                    System.err.println("💡 The app now uses SSL on port 465. If still failing, try:");
                    System.err.println("💡 1. Use a different Gmail account");
                    System.err.println("💡 2. Enable 'Less secure app access' temporarily");
                    System.err.println("💡 3. Use Google Workspace account if available");
                } else if (causeMsg.contains("ssl peer shut down incorrectly") || causeMsg.contains("eofexception")) {
                    System.err.println("💡 SSL connection issue - Gmail rejected the connection");
                    System.err.println("💡 This is common with cloud deployments. Try:");
                    System.err.println("💡 1. Use a different Gmail account");
                    System.err.println("💡 2. Enable less secure app access temporarily");
                    System.err.println("💡 3. Contact Google Workspace support if using GSuite");
                } else if (causeMsg.contains("timeout") || causeMsg.contains("connect")) {
                    System.err.println("💡 Connection issue - Gmail may be blocking connections from Render");
                    System.err.println("💡 Try using a different Gmail account or contact Google support");
                } else if (causeMsg.contains("550") || causeMsg.contains("recipient")) {
                    System.err.println("💡 Invalid recipient email address");
                }
            }
            throw new MailException("Failed to send email due to messaging error: " + e.getMessage(), e) {};
        } catch (Exception e) {
            System.err.println("❌ Failed to send email to: " + to);
            System.err.println("Error: " + e.getMessage());
            // Check for connection-related errors
            String errorMsg = e.getMessage() != null ? e.getMessage().toLowerCase() : "";
            if (errorMsg.contains("authentication") || errorMsg.contains("credentials")) {
                System.err.println("💡 Authentication issue detected");
                System.err.println("💡 Check SPRING_MAIL_USERNAME and SPRING_MAIL_PASSWORD");
                System.err.println("💡 For Gmail: Use App Password instead of regular password");
            } else if (errorMsg.contains("unsupported or unrecognized ssl message") || errorMsg.contains("sslexception")) {
                System.err.println("💡 SSL protocol error - Gmail rejected SSL connection");
                System.err.println("💡 The app uses SSL on port 465. Common solutions:");
                System.err.println("💡 1. Try a different Gmail account");
                System.err.println("💡 2. Enable 'Less secure app access' temporarily");
                System.err.println("💡 3. Use Google Workspace account if available");
                System.err.println("💡 4. Contact Google support for enterprise accounts");
            } else if (errorMsg.contains("ssl peer shut down incorrectly") || errorMsg.contains("eofexception")) {
                System.err.println("💡 SSL connection error - Gmail rejected the connection");
                System.err.println("💡 Common solutions:");
                System.err.println("💡 1. Try a different Gmail account");
                System.err.println("💡 2. Enable 'Less secure app access' temporarily");
                System.err.println("💡 3. Use Google Workspace account if available");
                System.err.println("💡 4. Contact Google support for enterprise accounts");
            } else if (errorMsg.contains("timeout") || errorMsg.contains("connect") || errorMsg.contains("smtp")) {
                System.err.println("💡 Connection issue detected. Gmail may be blocking cloud connections.");
                System.err.println("💡 Try using a different Gmail account or contact Google support.");
            } else if (errorMsg.contains("null") || errorMsg.contains("configuration")) {
                System.err.println("💡 Configuration issue - Check environment variables");
                System.err.println("💡 Required: SPRING_MAIL_USERNAME, SPRING_MAIL_PASSWORD");
            }
            e.printStackTrace();
            throw new MailException("Failed to send email: " + e.getMessage(), e) {};
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