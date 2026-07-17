package com.example.loginandregister.services;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

/**
 * Small helper used by OdmController and ChipsetController to save a
 * logo image uploaded via the "Create ODM" / "Add Chipset" forms.
 */
@Service
public class FileStorageService {

    /**
     * Saves the given file under uploads/logos/{subDir}/ with a random
     * name (so two uploads never overwrite each other) and returns the
     * public URL (starting with /uploads/...) that should be stored as
     * logoPath. Returns null if no file was actually sent.
     */
    public String saveLogo(MultipartFile file, String subDir) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }

        String uploadDir = "uploads/logos/" + subDir + "/";
        Files.createDirectories(Paths.get(uploadDir));

        String originalFileName = file.getOriginalFilename();
        String extension = "";
        if (originalFileName != null && originalFileName.contains(".")) {
            extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        }

        String fileName = UUID.randomUUID() + extension;
        Path filePath = Paths.get(uploadDir).resolve(fileName).normalize();

        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return "/uploads/logos/" + subDir + "/" + fileName;
    }
}
