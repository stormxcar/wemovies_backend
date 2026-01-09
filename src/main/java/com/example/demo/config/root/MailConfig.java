package com.example.demo.config.root;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class MailConfig {

    @Value("${spring.mail.host:smtp.gmail.com}")
    private String host;

    @Value("${spring.mail.port:587}")
    private int port;

    @Value("${spring.mail.username}")
    private String username;

    @Value("${spring.mail.password}")
    private String password;

    @Value("${spring.mail.provider:gmail}")
    private String provider;

    @Bean
    public JavaMailSender javaMailSender() {
        System.out.println("=== MAIL CONFIG DEBUG ===");
        System.out.println("Provider: Gmail SSL");
        System.out.println("Host: smtp.gmail.com");
        System.out.println("Port: 465");
        System.out.println("Username: " + (username != null ? "[SET]" : "[NULL]"));
        System.out.println("Password: " + (password != null && !password.isEmpty() ? "[SET]" : "[NULL/EMPTY]"));
        System.out.println("Environment: " + System.getenv("ENVIRONMENT"));
        System.out.println("=========================");

        // Validate required configuration
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalStateException("Email username is not configured. Please set SPRING_MAIL_USERNAME environment variable.");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalStateException("Email password is not configured. Please set SPRING_MAIL_PASSWORD environment variable.");
        }

        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(465); // Use SSL port instead of STARTTLS
        mailSender.setUsername(username);
        mailSender.setPassword(password);

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtps"); // Use SMTPS for SSL
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtps.ssl.enable", "true"); // Enable SSL instead of STARTTLS
        props.put("mail.smtps.ssl.trust", "*");

        // Enhanced SSL configuration for port 465
        props.put("mail.smtps.ssl.protocols", "TLSv1.2");
        props.put("mail.smtps.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtps.socketFactory.port", "465");
        props.put("mail.smtps.socketFactory.fallback", "false");

        // Connection timeout settings for cloud deployment
        props.put("mail.smtps.connectiontimeout", "30000");
        props.put("mail.smtps.timeout", "30000");
        props.put("mail.smtps.writetimeout", "30000");

        // Additional Gmail-specific settings to prevent SSL issues
        props.put("mail.smtps.ssl.checkserveridentity", "false");

        // Debug only in development
        String environment = System.getenv("ENVIRONMENT");
        if ("development".equals(environment) || "dev".equals(environment)) {
            props.put("mail.debug", "true");
        } else {
            props.put("mail.debug", "false");
        }

        return mailSender;
    }
}
