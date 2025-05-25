package com.example.demo.services;


import java.time.LocalDateTime;
import java.util.List;

public interface EmailService {
    void sendEmail(String to, String subject, String content);
}
