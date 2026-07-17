package com.example.loginandregister.services;

import com.example.loginandregister.model.Odm;
import com.example.loginandregister.model.User;
import com.example.loginandregister.repository.OdmRepo;
import com.example.loginandregister.repository.ProjectRepo;
import com.example.loginandregister.repository.UserRepo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class OdmService {
    @Autowired
    private OdmRepo odmRepo;

    @Autowired
    private ProjectRepo projectRepo;

    @Autowired
    private UserRepo userRepo;

    public List<Odm> getAllOdms() {
        return odmRepo.findAll();
    }

    public boolean existsByName(String name) {
        return odmRepo.existsByName(name);
    }

    public Odm findByEmail(String email) {
        return odmRepo.findByEmail(email);
    }

    public Odm findByName(String name) {
        return odmRepo.findByName(name);
    }

    @Transactional
    public Odm saveOdm(Odm odm) {

        if (odmRepo.existsByName(odm.getName())) {
            return null;
        }

        if (odm.getLogoPath() == null || odm.getLogoPath().isEmpty()) {
            odm.setLogoPath("/images/Group 8057.png");
        }


        odm.setLastUpdate(LocalDate.now());
        return odmRepo.save(odm);
    }

    @Transactional
    public boolean deleteOdm(String odmName) {
        if (projectRepo.existsByOdmName(odmName)) {
            return false; // projects exist, block delete
        }
        odmRepo.deleteByName(odmName);
        return true;
    }

    @Transactional
    public boolean renameOdm(String oldName, String newName) {
        if (odmRepo.existsByName(newName)) {
            return false; // naam already liya hua hai
        }
        Odm odm = odmRepo.findByName(oldName);
        if (odm == null) {
            return false;
        }
        odm.setName(newName);
        odm.setLastUpdate(LocalDate.now());
        odmRepo.save(odm);

        // Linked users ka odmName bhi sync karo
        List<User> linkedUsers = userRepo.findByOdmName(oldName);
        for (User u : linkedUsers) {
            u.setOdmName(newName);
            userRepo.save(u);
        }
        return true;
    }


    public List<Odm> searchByName(String query) {
        if (query == null || query.isBlank()) {
            return odmRepo.findAll();
        }
        return odmRepo.findByNameContainingIgnoreCase(query.trim());
    }



}
