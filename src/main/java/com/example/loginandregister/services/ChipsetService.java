package com.example.loginandregister.services;

import com.example.loginandregister.model.Chipset;
import com.example.loginandregister.model.Project;
import com.example.loginandregister.repository.ChipsetRepo;
import com.example.loginandregister.repository.ProjectRepo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ChipsetService {
    @Autowired
    ChipsetRepo chipsetRepo;

    @Autowired
    ProjectRepo projectRepo;

    public List<Chipset> getAllChipsets(){
        return chipsetRepo.findAll();

    }

    public boolean hasProjectsByChipset(String chipset) {
        return projectRepo.existsByChipset(chipset);
    }

    public List<Project> getProjectsByChipset(String chipset) {
        return projectRepo.findByChipset(chipset);
    }

    public boolean existsByName(String name) {
        return chipsetRepo.existsByName(name);
    }

    public Chipset findByName(String name) {
        return chipsetRepo.findByName(name);
    }

    @Transactional
    public Chipset saveChipset(Chipset chipset) {
        if (chipsetRepo.existsByName(chipset.getName())) {
            return null;
        }

        if (chipset.getLogoPath() == null || chipset.getLogoPath().isEmpty()) {
            chipset.setLogoPath("/images/Group 8057.png");
        }

        chipset.setLastUpdate(LocalDate.now());
        return chipsetRepo.save(chipset);
    }

    @Transactional
    public boolean deleteChipset(String name) {
        if (projectRepo.existsByChipset(name)) {
            return false; // projects using this chipset exist, block delete
        }
        chipsetRepo.deleteByName(name);
        return true;
    }

    @Transactional
    public boolean renameChipset(String oldName, String newName) {
        if (chipsetRepo.existsByName(newName)) {
            return false; // naam already liya hua hai
        }
        Chipset chipset = chipsetRepo.findByName(oldName);
        if (chipset == null) {
            return false;
        }
        chipset.setName(newName);
        chipset.setLastUpdate(LocalDate.now());
        chipsetRepo.save(chipset);

        // Linked projects ka chipset naam bhi sync karo
        List<Project> linkedProjects = projectRepo.findByChipset(oldName);
        for (Project p : linkedProjects) {
            p.setChipset(newName);
            projectRepo.save(p);
        }
        return true;
    }


    public List<Chipset> searchByName(String query) {
        if (query == null || query.isBlank()) {
            return chipsetRepo.findAll();
        }
        return chipsetRepo.findByNameContainingIgnoreCase(query.trim());
    }

}
