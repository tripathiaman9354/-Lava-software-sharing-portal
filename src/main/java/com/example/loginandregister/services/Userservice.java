package com.example.loginandregister.services;

import com.example.loginandregister.model.User;
import com.example.loginandregister.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
public class Userservice {
    @Autowired
    UserRepo userRepo;

    private static final String PASSWORD_CHARS =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789@#$%";
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    public boolean sendData(User user) {
        User users1 = userRepo.findByEmail(user.getEmail());
        if (users1 != null && users1.getPassword().equals(user.getPassword())) {
            return true;
        }
        return false;
    }

    public boolean emailExists(String email) {
        return userRepo.existsByEmail(email);
    }

    public User findByEmail(String email) {
        return userRepo.findByEmail(email);
    }

    public boolean resetPassword(String email, String newPassword) {
        User user = userRepo.findByEmail(email);
        if (user == null) {
            return false;
        }
        user.setPassword(newPassword);
        userRepo.save(user);
        return true;
    }

    public User authenticate(User user) {
        User existing = userRepo.findByEmail(user.getEmail());
        if (existing != null && existing.getPassword().equals(user.getPassword())) {
            return existing;
        }
        return null;
    }

    public User createOdmUser(String email, String odmName, String password) {
        User user = new User();
        user.setEmail(email);
        user.setPassword(password);
        user.setRole("USER");
        user.setOdmName(odmName);
        return userRepo.save(user);
    }

    public User createOrUpdateOdmUser(String email, String odmName, String password) {
        User user = userRepo.findByEmail(email);
        if (user == null) {
            return createOdmUser(email, odmName, password);
        }

        user.setRole("USER");
        user.setOdmName(odmName);
        user.setPassword(password);
        return userRepo.save(user);
    }

    public String generateTemporaryPassword() {
        StringBuilder password = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            int index = SECURE_RANDOM.nextInt(PASSWORD_CHARS.length());
            password.append(PASSWORD_CHARS.charAt(index));
        }
        return password.toString();
    }
}
