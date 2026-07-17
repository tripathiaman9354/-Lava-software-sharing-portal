package com.example.loginandregister.controller;

import com.example.loginandregister.model.Odm;
import com.example.loginandregister.model.User;
import com.example.loginandregister.services.EmailService;
import com.example.loginandregister.services.FileStorageService;
import com.example.loginandregister.services.OdmService;
import com.example.loginandregister.services.ProjectService;
import com.example.loginandregister.services.Userservice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class OdmController {

    @Autowired
    private OdmService odmService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private Userservice userservice;

    @Autowired
    private EmailService emailService;

    @Autowired
    private FileStorageService fileStorageService;

    @GetMapping("/dashboard/odm")
    public String odmPage(Model model, HttpSession session) {
        List<Odm> odms = odmService.getAllOdms();
        Map<String, Boolean> odmProjectMap = new HashMap<>();

        for (Odm odm : odms) {
            boolean hasData = projectService.hasProjectsByOdm(odm.getName());
            odmProjectMap.put(odm.getName(), hasData);
        }
        model.addAttribute("odms", odms);
        model.addAttribute("odm", new Odm());
        model.addAttribute("odmProjectMap", odmProjectMap);
        model.addAttribute("displayName", session.getAttribute("displayName"));
        model.addAttribute("displayImage", session.getAttribute("displayImage"));
        return "odm";
    }

    @PostMapping("/odm/create")
    public String createOdm(@ModelAttribute Odm odm ,
                            @RequestParam(value = "logoFile", required = false) MultipartFile logoFile,
                            Model model,
                            RedirectAttributes redirectAttributes)
        {
            if (odmService.existsByName(odm.getName())) {
                Odm existingOdm = odmService.findByName(odm.getName());
                User existingUser = userservice.findByEmail(odm.getEmail());
                if (existingUser != null && "ADMIN".equalsIgnoreCase(existingUser.getRole())) {
                    redirectAttributes.addFlashAttribute(
                            "emailError",
                            "Admin email cannot be used for ODM login"
                    );
                    return "redirect:/dashboard/odm";
                }

                if (existingOdm != null
                        && existingOdm.getEmail() != null
                        && existingOdm.getEmail().equalsIgnoreCase(odm.getEmail())
                        && (existingUser == null
                        || existingUser.getOdmName() == null
                        || existingUser.getOdmName().isBlank())) {
                    createUserAndSendCredentials(existingOdm, redirectAttributes);
                    return "redirect:/dashboard/odm";
                }

                redirectAttributes.addFlashAttribute(
                        "nameError",
                        "ODM name already exists"
                );
                return "redirect:/dashboard/odm";
            }

            User existingUser = userservice.findByEmail(odm.getEmail());
            if (existingUser != null && "ADMIN".equalsIgnoreCase(existingUser.getRole())) {
                redirectAttributes.addFlashAttribute(
                        "emailError",
                        "Admin email cannot be used for ODM login"
                );
                return "redirect:/dashboard/odm";
            }

            if (existingUser != null && existingUser.getOdmName() != null
                    && !existingUser.getOdmName().isBlank()) {
                redirectAttributes.addFlashAttribute(
                        "emailError",
                        "An account already exists with this email"
                );
                return "redirect:/dashboard/odm";
            }

            if (logoFile != null && !logoFile.isEmpty()) {
                try {
                    odm.setLogoPath(fileStorageService.saveLogo(logoFile, "odm"));
                } catch (IOException e) {
                    // logo save failed, ODM will just fall back to the default logo
                }
            }

            Odm savedOdm = odmService.saveOdm(odm);

            if (savedOdm == null) {
                redirectAttributes.addFlashAttribute(
                        "nameError",
                        "ODM name already exists"
                );

            }
            else {
                createUserAndSendCredentials(savedOdm, redirectAttributes);
            }
            return "redirect:/dashboard/odm";
        }

    private void createUserAndSendCredentials(Odm odm, RedirectAttributes redirectAttributes) {
        String temporaryPassword = userservice.generateTemporaryPassword();
        userservice.createOrUpdateOdmUser(odm.getEmail(), odm.getName(), temporaryPassword);

        try {
            emailService.sendOdmCredentialsEmail(odm.getEmail(), odm.getName(), temporaryPassword);
            redirectAttributes.addFlashAttribute(
                    "success",
                    "ODM created and login credentials sent to email"
            );
        } catch (Exception exception) {
            redirectAttributes.addFlashAttribute(
                    "warning",
                    "ODM/user account created, but email could not be sent. Temporary password: "
                            + temporaryPassword
            );
        }
    }

    @PostMapping("/dashboard/odm/{name}/delete")
    public String deleteOdm(@PathVariable String name, RedirectAttributes redirectAttributes) {
        boolean deleted = odmService.deleteOdm(name);
        if (!deleted) {
            redirectAttributes.addFlashAttribute("warning", "Cannot delete ODM. Projects exist!");
        } else {
            redirectAttributes.addFlashAttribute("success", "ODM deleted successfully");
        }
        return "redirect:/dashboard/odm";
    }

    @PostMapping("/dashboard/odm/{name}/rename")
    public String renameOdm(@PathVariable String name,
                            @RequestParam String newName,
                            RedirectAttributes redirectAttributes) {
        boolean renamed = odmService.renameOdm(name, newName);
        if (!renamed) {
            redirectAttributes.addFlashAttribute("warning", "Could not rename. Name may already exist.");
        } else {
            redirectAttributes.addFlashAttribute("success", "ODM renamed successfully");
        }
        return "redirect:/dashboard/odm";
    }

}
