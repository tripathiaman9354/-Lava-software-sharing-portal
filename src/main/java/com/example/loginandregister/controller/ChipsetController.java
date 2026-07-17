package com.example.loginandregister.controller;

import com.example.loginandregister.model.Chipset;
import com.example.loginandregister.model.Project;
import com.example.loginandregister.services.ChipsetService;
import com.example.loginandregister.services.FileStorageService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.example.loginandregister.model.Folder;
import com.example.loginandregister.repository.FolderRepo;
import com.example.loginandregister.repository.ProjectRepo;
import java.util.ArrayList;

import java.io.IOException;
import java.util.List;

@Controller
public class ChipsetController{
    @Autowired
    ChipsetService chipsetService;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private ProjectRepo projectRepo;

    @Autowired
    private FolderRepo folderRepo;

    @GetMapping("/dashboard/chipset")
    public String chipset(Model model, HttpSession session) {
        List<Chipset> chipsets = chipsetService.getAllChipsets();
        model.addAttribute("chipsets",chipsets);
        model.addAttribute("chipset",new Chipset());
        model.addAttribute("displayName", session.getAttribute("displayName"));
        return "chipset";
    }

    @GetMapping("/dashboard/chipsets/{chipset}")
    public String project(@PathVariable String chipset, HttpSession session, Model model) {
        List<Project> projects = chipsetService.getProjectsByChipset(chipset);
        model.addAttribute("chipsetName", chipset);
        model.addAttribute("projects", projects);
        model.addAttribute("displayName", session.getAttribute("displayName"));
        return "chipsetProject";
    }

    @GetMapping("/dashboard/chipsets/{chipset}/projects/{projectId}")
    public String chipsetProjectFolders(
            @PathVariable String chipset,
            @PathVariable Long projectId,
            HttpSession session,
            Model model) {
        Project project = projectRepo.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        List<Folder> folders = folderRepo.findByProjectId(projectId);

        model.addAttribute("chipsetName", chipset);
        model.addAttribute("project", project);
        model.addAttribute("folders", folders);
        model.addAttribute("displayName", session.getAttribute("displayName"));
        model.addAttribute("displayImage", session.getAttribute("displayImage"));
        return "chipsetProjectFolders";
    }

    @GetMapping("/dashboard/chipsets/{chipset}/projects/{projectId}/folders/{folderId}")
    public String chipsetFolderFiles(
            @PathVariable String chipset,
            @PathVariable Long projectId,
            @PathVariable Long folderId,
            HttpSession session,
            Model model) {
        Project project = projectRepo.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        Folder folder = folderRepo.findById(folderId)
                .orElseThrow(() -> new RuntimeException("Folder not found"));

        List<Folder> moveTargets = new ArrayList<>();
        for (Folder f : folderRepo.findByProjectId(projectId)) {
            if (!f.getId().equals(folderId)) {
                moveTargets.add(f);
            }
        }

        model.addAttribute("chipsetName", chipset);
        model.addAttribute("project", project);
        model.addAttribute("folder", folder);
        model.addAttribute("moveTargets", moveTargets);
        model.addAttribute("displayName", session.getAttribute("displayName"));
        model.addAttribute("displayImage", session.getAttribute("displayImage"));
        return "chipsetFolderFiles";
    }

    @PostMapping("/chipset/create")
    public String createChipset(@ModelAttribute Chipset chipset,
                                 @RequestParam(value = "logoFile", required = false) MultipartFile logoFile,
                                 RedirectAttributes redirectAttributes) {

        if (chipsetService.existsByName(chipset.getName())) {
            redirectAttributes.addFlashAttribute("nameError", "Chipset name already exists");
            return "redirect:/dashboard/chipset";
        }

        if (logoFile != null && !logoFile.isEmpty()) {
            try {
                chipset.setLogoPath(fileStorageService.saveLogo(logoFile, "chipset"));
            } catch (IOException e) {
            }
        }

        Chipset saved = chipsetService.saveChipset(chipset);

        if (saved == null) {
            redirectAttributes.addFlashAttribute("nameError", "Chipset name already exists");
        } else {
            redirectAttributes.addFlashAttribute("success", "Chipset added successfully");
        }
        return "redirect:/dashboard/chipset";
    }

    @PostMapping("/dashboard/chipset/{name}/delete")
    public String deleteChipset(@PathVariable String name, RedirectAttributes redirectAttributes) {
        boolean deleted = chipsetService.deleteChipset(name);
        if (!deleted) {
            redirectAttributes.addFlashAttribute("warning", "Cannot delete Chipset. Projects exist!");
        } else {
            redirectAttributes.addFlashAttribute("success", "Chipset deleted successfully");
        }
        return "redirect:/dashboard/chipset";
    }

    @PostMapping("/dashboard/chipset/{name}/rename")
    public String renameChipset(@PathVariable String name,
                                 @RequestParam String newName,
                                 RedirectAttributes redirectAttributes) {
        boolean renamed = chipsetService.renameChipset(name, newName);
        if (!renamed) {
            redirectAttributes.addFlashAttribute("warning", "Could not rename. Name may already exist.");
        } else {
            redirectAttributes.addFlashAttribute("success", "Chipset renamed successfully");
        }
        return "redirect:/dashboard/chipset";
    }

}
