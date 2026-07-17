package com.example.loginandregister.repository;

import com.example.loginandregister.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectRepo extends JpaRepository<Project,Long> {
    List<Project> findByOdmName(String odmName);
    boolean existsByOdmName(String odmName);

    boolean existsByChipset(String chipset);

    List<Project> findByChipset(String chipset);

}
