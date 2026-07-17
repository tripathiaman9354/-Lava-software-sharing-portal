package com.example.loginandregister.services;

import com.example.loginandregister.model.Odm;
import com.example.loginandregister.model.Project;
import com.example.loginandregister.repository.ProjectRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectService {
    @Autowired
    ProjectRepo projectRepo;




    public List<Project> getProjectsByUrlName(String name) {
        return projectRepo.findByOdmName(name);
    }

    public boolean hasProjectsByOdm(String odmName) {
        return projectRepo.existsByOdmName(odmName);
    }

}
