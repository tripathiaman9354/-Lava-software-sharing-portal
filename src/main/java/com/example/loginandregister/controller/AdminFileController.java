package com.example.loginandregister.controller;

import com.example.loginandregister.model.FileEntity;
import com.example.loginandregister.model.Folder;
import com.example.loginandregister.repository.FileEntityRepo;
import com.example.loginandregister.repository.FolderRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Controller
@org.springframework.web.bind.annotation.RequestMapping("/dashboard/files")
public class AdminFileController {

    @Autowired
    private FileEntityRepo fileEntityRepo;

    @Autowired
    private FolderRepo folderRepo;

    @PostMapping("/{fileId}/move")
    public String moveFile(@PathVariable Long fileId,
                            @RequestParam Long targetFolderId,
                            RedirectAttributes redirectAttributes) {

        FileEntity file = fileEntityRepo.findById(fileId)
                .orElseThrow(() -> new RuntimeException("File not found"));
        Folder targetFolder = folderRepo.findById(targetFolderId)
                .orElseThrow(() -> new RuntimeException("Target folder not found"));

        String odmName = file.getFolder().getProject().getOdmName();
        Long projectId = file.getFolder().getProject().getId();

        try {
            Path oldPath = Paths.get(file.getFilePath());
            String newDir = "uploads/" + targetFolder.getId() + "/";
            Files.createDirectories(Paths.get(newDir));
            Path newPath = Paths.get(newDir).resolve(file.getName()).normalize();

            Files.move(oldPath, newPath, StandardCopyOption.REPLACE_EXISTING);

            file.setFilePath(newPath.toString());
            file.setFolder(targetFolder);
            fileEntityRepo.save(file);

            redirectAttributes.addFlashAttribute("success", "File moved successfully");
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("warning", "Failed to move file");
        }

        return "redirect:/dashboard/odm/" + odmName + "/projects/" + projectId + "/folders/" + targetFolder.getId();
    }

    @PostMapping("/{fileId}/copy")
    public String copyFile(@PathVariable Long fileId,
                            @RequestParam Long targetFolderId,
                            RedirectAttributes redirectAttributes) {

        FileEntity file = fileEntityRepo.findById(fileId)
                .orElseThrow(() -> new RuntimeException("File not found"));
        Folder targetFolder = folderRepo.findById(targetFolderId)
                .orElseThrow(() -> new RuntimeException("Target folder not found"));

        String odmName = file.getFolder().getProject().getOdmName();
        Long projectId = file.getFolder().getProject().getId();
        Long sourceFolderId = file.getFolder().getId();

        try {
            Path oldPath = Paths.get(file.getFilePath());
            String newDir = "uploads/" + targetFolder.getId() + "/";
            Files.createDirectories(Paths.get(newDir));
            Path newPath = Paths.get(newDir).resolve(file.getName()).normalize();

            Files.copy(oldPath, newPath, StandardCopyOption.REPLACE_EXISTING);

            FileEntity copy = new FileEntity();
            copy.setName(file.getName());
            copy.setFilePath(newPath.toString());
            copy.setFolder(targetFolder);
            fileEntityRepo.save(copy);

            redirectAttributes.addFlashAttribute("success", "File copied successfully");
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("warning", "Failed to copy file");
        }

        return "redirect:/dashboard/odm/" + odmName + "/projects/" + projectId + "/folders/" + sourceFolderId;
    }

    @PostMapping("/{fileId}/delete")
    public String deleteFile(@PathVariable Long fileId, RedirectAttributes redirectAttributes) {

        FileEntity file = fileEntityRepo.findById(fileId)
                .orElseThrow(() -> new RuntimeException("File not found"));

        String odmName = file.getFolder().getProject().getOdmName();
        Long projectId = file.getFolder().getProject().getId();
        Long folderId = file.getFolder().getId();

        try {
            Files.deleteIfExists(Paths.get(file.getFilePath()));
        } catch (IOException ignored) {
            // if the physical file is already missing we still remove the DB record
        }
        fileEntityRepo.delete(file);

        redirectAttributes.addFlashAttribute("success", "File deleted successfully");
        return "redirect:/dashboard/odm/" + odmName + "/projects/" + projectId + "/folders/" + folderId;
    }

    @GetMapping("/{fileId}/download")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long fileId) throws IOException {
        FileEntity file = fileEntityRepo.findById(fileId)
                .orElseThrow(() -> new RuntimeException("File not found"));

        Path path = Paths.get(file.getFilePath());
        Resource resource = new UrlResource(path.toUri());

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"")
                .body(resource);
    }
}
