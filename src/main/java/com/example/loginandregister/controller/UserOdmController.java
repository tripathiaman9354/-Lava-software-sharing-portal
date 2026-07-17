package com.example.loginandregister.controller;


import com.example.loginandregister.model.FileEntity;
import com.example.loginandregister.model.Folder;
import com.example.loginandregister.model.Project;
import com.example.loginandregister.repository.FileEntityRepo;
import com.example.loginandregister.repository.FolderRepo;
import com.example.loginandregister.repository.ProjectRepo;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;


import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Controller
public class UserOdmController {

    @Autowired
    private FileEntityRepo fileEntityRepo;

    @Autowired
    private FolderRepo folderRepo;

    @Autowired
    private ProjectRepo projectRepo;

    private boolean isOwnedByOdm(String projectOdmName, String sessionOdmName) {
        if (projectOdmName == null || sessionOdmName == null) {
            return false;
        }
        return projectOdmName.trim().equalsIgnoreCase(sessionOdmName.trim());
    }

    @GetMapping("/odmdashboard")
    public String project() {
        return "redirect:/user-dashboard";
    }

    @GetMapping("/odmdashboard/projects")
    public String insideProject(HttpSession session) {
        String odmName = (String) session.getAttribute("loggedinOdmName");
        List<Project> projects = projectRepo.findByOdmName(odmName);
        if (projects.isEmpty()) {
            return "redirect:/user-dashboard";
        }
        return "redirect:/odmdashboard/projects/" + projects.get(0).getId();
    }

    @GetMapping("/odmdashboard/projects/{projectId}")
    public String insideProject(
            @PathVariable Long projectId,
            HttpSession session,
            Model model) {
        String odmName = (String) session.getAttribute("loggedinOdmName");
        Project project = projectRepo.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        if (!isOwnedByOdm(project.getOdmName(), odmName)) {
            return "redirect:/user-dashboard";
        }

        List<Folder> folders = folderRepo.findByProjectIdAndProjectOdmNameAndNameNot(
                projectId, odmName, "Production");
        model.addAttribute("folders", folders);
        model.addAttribute("project", project);
        model.addAttribute("title", project.getProjectName());
        model.addAttribute("displayName", session.getAttribute("displayName"));
        model.addAttribute("displayImage", session.getAttribute("displayImage"));
        return "ProjectPage";
    }


    @PostMapping("/odmdashboard/projects/{folderId}/uploadfiles")
    @ResponseBody
    public ResponseEntity<String> uploadFile(
            @RequestParam("file") MultipartFile file,
            @PathVariable Long folderId,
            HttpSession session) {

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Please select a file");
        }

        try {
            Folder folder = folderRepo.findById(folderId)
                    .orElseThrow(() -> new RuntimeException("Folder not found"));
            String odmName = (String) session.getAttribute("loggedinOdmName");

            if (folder.getProject() == null || !isOwnedByOdm(folder.getProject().getOdmName(), odmName)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("You cannot upload files to this folder");
            }

            String uploadDir = "uploads/" + folder.getId() + "/";
            Files.createDirectories(Paths.get(uploadDir));

            String originalFileName = file.getOriginalFilename();
            String fileName = (originalFileName == null || originalFileName.isBlank())
                    ? "file"
                    : Paths.get(originalFileName).getFileName().toString();
            Path filePath = Paths.get(uploadDir).resolve(fileName).normalize();

            if (!filePath.startsWith(Paths.get(uploadDir).normalize())) {
                return ResponseEntity.badRequest().body("Invalid file name");
            }

            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            FileEntity fileEntity = new FileEntity();
            fileEntity.setName(fileName);
            fileEntity.setFilePath(filePath.toString());
            fileEntity.setFolder(folder);

            fileEntityRepo.save(fileEntity);

            return ResponseEntity.ok("File uploaded successfully");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("File upload failed");
        }
    }


    @GetMapping("/odmdashboard/projects/{folderId}/uploadfiles")
    public String uploadProjectFiles(
            @PathVariable Long folderId,
            HttpSession session,
            Model model) {

        Folder folder = folderRepo.findById(folderId)
                .orElseThrow(() -> new RuntimeException("Folder not found"));
        String odmName = (String) session.getAttribute("loggedinOdmName");

        if (folder.getProject() == null || !isOwnedByOdm(folder.getProject().getOdmName(), odmName)) {
            return "redirect:/user-dashboard";
        }

        model.addAttribute("title", "Dashboard 2");
        model.addAttribute("folder", folder);
        model.addAttribute("displayName", session.getAttribute("displayName"));
        model.addAttribute("displayImage", session.getAttribute("displayImage"));
        return "uploadSoftware";
    }

}