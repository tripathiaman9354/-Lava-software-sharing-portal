package com.example.loginandregister.services;

import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
public class OtpService {

    private final SecureRandom random = new SecureRandom();
    public static final long OTP_VALID_DURATION_MS = 5 * 60 * 1000; // 5 minutes

    public String generateOtp() {
        int otp = 100000 + random.nextInt(900000); // 6-digit OTP
        return String.valueOf(otp);
    }
}
