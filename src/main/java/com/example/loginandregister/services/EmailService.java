package com.example.loginandregister.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendOtpEmail(String toEmail, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Password Reset OTP - Lava Software Portal");
        message.setText("Your OTP for password reset is: " + otp +
                "\n\nThis OTP is valid for 5 minutes. If you did not request this, please ignore this email.");
        mailSender.send(message);
    }

    public void sendOdmCredentialsEmail(String toEmail, String odmName, String password) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("ODM Account Created - Lava Software Portal");
        message.setText("Your ODM account has been created.\n\n"
                + "ODM: " + odmName + "\n"
                + "Email: " + toEmail + "\n"
                + "Password: " + password + "\n\n"
                + "Please log in and change your password after first login.");
        mailSender.send(message);
    }
}
