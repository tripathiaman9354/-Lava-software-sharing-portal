package com.example.loginandregister.repository;

import com.example.loginandregister.model.Folder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FolderRepo extends JpaRepository<Folder, Long> {

    // Admin view: ALL root folders of a project (including "Production")
    List<Folder> findByProjectId(Long projectId);

    // ODM user view: root folders of a project, but never "Production"
    List<Folder> findByProjectIdAndProjectOdmNameAndNameNot(
            Long projectId, String odmName, String name);
}