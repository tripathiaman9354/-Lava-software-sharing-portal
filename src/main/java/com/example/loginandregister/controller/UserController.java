package com.example.loginandregister.controller;

import com.example.loginandregister.model.Odm;
import com.example.loginandregister.model.User;
import com.example.loginandregister.repository.ProjectRepo;
import com.example.loginandregister.services.EmailService;
import com.example.loginandregister.services.OdmService;
import com.example.loginandregister.services.OtpService;
import com.example.loginandregister.services.Userservice;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

//@RestController
@Controller
public class UserController {

    @Autowired
    Userservice userservice;
    @Autowired
    private OdmService odmService;
    @Autowired
    private EmailService emailService;
    @Autowired
    private OtpService otpService;
    @Autowired
    private ProjectRepo projectRepo;

    @GetMapping("/login")
    public String login(Model model){
        model.addAttribute("userlogin", new User());
        return "login";
    }


    @PostMapping("/login")
    public String login_request(@ModelAttribute("userlogin") User user,
                                HttpSession session,
                                Model model){
        User authenticatedUser = userservice.authenticate(user);
        if (authenticatedUser != null) {
            session.setAttribute("loggedinUser", authenticatedUser.getEmail());
            session.setAttribute("role", authenticatedUser.getRole());

            if ("ADMIN".equalsIgnoreCase(authenticatedUser.getRole())) {
                // Admin ke liye odmName field hi uska naam maan lo
                session.setAttribute("displayName", authenticatedUser.getOdmName());
                session.setAttribute("displayImage", "/images/Ellipse 1.png"); // fixed admin image
                return "redirect:/admin-dashboard";
            } else {
                // User ke liye odmName se Odm table lookup karo
                Odm odm = odmService.findByName(authenticatedUser.getOdmName());
                session.setAttribute("loggedinOdmName", authenticatedUser.getOdmName()); // NEW LINE
                if (odm != null) {
                    session.setAttribute("displayName", odm.getName());
                    session.setAttribute("displayImage", odm.getLogoPath());
                }
                return "redirect:/user-dashboard";
            }
        }
        else{
            model.addAttribute("error", "You have entered wrong password!");
            model.addAttribute("userlogin", new User());
            return "login";
        }
    }


    @GetMapping("/logout")
    public String logout(HttpSession session,
                         jakarta.servlet.http.HttpServletResponse response){
        session.invalidate();
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
        return "redirect:/login";
    }


    @GetMapping("/forgot-password")
    public String forgotPassword() {
        return "forgot-password";
    }

    @PostMapping("/forgot-password")
    public String forgotPasswordSubmit(@RequestParam("email") String email,
                                        HttpSession session,
                                        Model model) {
        if (!userservice.emailExists(email)) {
            model.addAttribute("error", "No account found with this email");
            return "forgot-password";
        }

        String otp = otpService.generateOtp();
        session.setAttribute("resetEmail", email);
        session.setAttribute("otp", otp);
        session.setAttribute("otpGeneratedTime", System.currentTimeMillis());
        session.removeAttribute("otpVerified");

        emailService.sendOtpEmail(email, otp);

        return "redirect:/verify-otp";
    }

    @GetMapping("/verify-otp")
    public String verifyOtpPage(HttpSession session) {
        if (session.getAttribute("resetEmail") == null) {
            return "redirect:/forgot-password";
        }
        return "verify-otp";
    }

    @PostMapping("/verify-otp")
    public String verifyOtpSubmit(@RequestParam("otp") String otp,
                                   HttpSession session,
                                   Model model) {
        String email = (String) session.getAttribute("resetEmail");
        String actualOtp = (String) session.getAttribute("otp");
        Long generatedTime = (Long) session.getAttribute("otpGeneratedTime");

        if (email == null || actualOtp == null) {
            return "redirect:/forgot-password";
        }

        boolean expired = generatedTime == null
                || (System.currentTimeMillis() - generatedTime) > OtpService.OTP_VALID_DURATION_MS;

        if (expired) {
            model.addAttribute("error", "OTP has expired. Please request a new one.");
            session.removeAttribute("otp");
            session.removeAttribute("otpGeneratedTime");
            return "verify-otp";
        }

        if (!actualOtp.equals(otp.trim())) {
            model.addAttribute("error", "Invalid OTP. Please try again.");
            return "verify-otp";
        }

        session.setAttribute("otpVerified", true);
        session.removeAttribute("otp");
        session.removeAttribute("otpGeneratedTime");

        return "redirect:/reset-password";
    }

    @PostMapping("/resend-otp")
    public String resendOtp(HttpSession session, Model model) {
        String email = (String) session.getAttribute("resetEmail");
        if (email == null) {
            return "redirect:/forgot-password";
        }

        String otp = otpService.generateOtp();
        session.setAttribute("otp", otp);
        session.setAttribute("otpGeneratedTime", System.currentTimeMillis());
        emailService.sendOtpEmail(email, otp);

        model.addAttribute("success", "A new OTP has been sent to your email.");
        return "verify-otp";
    }

    @GetMapping("/reset-password")
    public String resetPassword(HttpSession session) {
        if (session.getAttribute("resetEmail") == null
                || session.getAttribute("otpVerified") == null) {
            return "redirect:/forgot-password";
        }
        return "reset-password";
    }

    @PostMapping("/reset-password")
    public String resetPasswordSubmit(@RequestParam("password") String password,
                                       @RequestParam("confirmPassword") String confirmPassword,
                                       HttpSession session,
                                       Model model) {
        String email = (String) session.getAttribute("resetEmail");
        Object otpVerified = session.getAttribute("otpVerified");

        if (email == null || otpVerified == null) {
            return "redirect:/forgot-password";
        }

        if (password == null || password.isBlank()) {
            model.addAttribute("error", "Password cannot be empty");
            return "reset-password";
        }
        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "Passwords do not match");
            return "reset-password";
        }

        userservice.resetPassword(email, password);
        session.removeAttribute("resetEmail");
        session.removeAttribute("otpVerified");
        model.addAttribute("userlogin", new User());
        model.addAttribute("success", "Password reset successfully. Please log in.");
        return "login";
    }


    @GetMapping("/user-dashboard")
    public String userDashboard(HttpSession session, Model model) {
        String displayName = (String) session.getAttribute("displayName");
        if (displayName == null || displayName.isBlank()) {
            return "redirect:/login";
        }

        // Actual odmName field se projects fetch karna hai (User entity ka odmName)
        String odmName = (String) session.getAttribute("loggedinOdmName");

        model.addAttribute("projects", projectRepo.findByOdmName(odmName));
        model.addAttribute("displayName", displayName);
        model.addAttribute("displayImage", session.getAttribute("displayImage"));
        return "user-dashboard";
    }

    @GetMapping("/admin-dashboard")
    public String adminDashboard(HttpSession session, Model model) {
        if (!"ADMIN".equalsIgnoreCase((String) session.getAttribute("role"))) {
            return "redirect:/login";
        }
        model.addAttribute("displayName", session.getAttribute("displayName"));
        return "admin-dashboard"; // templates/admin-dashboard.html
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        return "redirect:/admin-dashboard";
    }


}
