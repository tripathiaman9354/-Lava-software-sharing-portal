package com.example.loginandregister.controller;

import com.example.loginandregister.model.Folder;
import com.example.loginandregister.model.Odm;
import com.example.loginandregister.model.Project;
import com.example.loginandregister.repository.FolderRepo;
import com.example.loginandregister.repository.OdmRepo;
import com.example.loginandregister.repository.ProjectRepo;
import com.example.loginandregister.services.ChipsetService;
import com.example.loginandregister.services.ProjectService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
public class CreateProjectController {

    @Autowired
    private ProjectRepo projectRepo;

    @Autowired
    private OdmRepo odmRepo;

    @Autowired
    private FolderRepo folderRepo;

    @Autowired
    ProjectService projectService;

    @Autowired
    private ChipsetService chipsetService;


    @GetMapping("/dashboard/odm/{name}")
    public String odmProject(
            @PathVariable String name,
            HttpSession session,
            Model model) {
        List<Project> projects = projectService.getProjectsByUrlName(name);
        model.addAttribute("title", name);
        model.addAttribute("name", name);
        model.addAttribute("createProject", new Project());
        model.addAttribute("displayName", session.getAttribute("displayName"));
        model.addAttribute("displayImage", session.getAttribute("displayImage"));
        model.addAttribute("chipsets", chipsetService.getAllChipsets());

        if (projects == null || projects.isEmpty()) {
            return "snapdragontech";
        } else {
            model.addAttribute("projects", projects);
            return "Project";
        }
    }



    @PostMapping("/dashboard/odm/{name}/")
    public String createProject(
            @PathVariable String name,
            @ModelAttribute("createProject") Project project) {
        project.setOdmName(name);

        Odm odm = odmRepo.findByName(name);
        if (odm != null) {
            project.setOdm(odm);
        }

        List<String> rootFolders = List.of(
                "Official SW",
                "Demo SW",
                "IR SW",
                "Production"
        );
        List<Folder> folders = new ArrayList<>();
        for (String folderName : rootFolders) {
            Folder folder = new Folder();
            folder.setName(folderName);
            folder.setProject(project);
            folders.add(folder);
        }
        project.setFolders(folders);
        projectRepo.save(project);
        return "redirect:/dashboard/odm/" + name + "/";
    }

    @GetMapping("/dashboard/odm/{name}/")
    public String projects(
            @PathVariable String name,
            HttpSession session,
            Model model) {
        List<Project> projects = projectService.getProjectsByUrlName(name);
        model.addAttribute("projects", projects);
        model.addAttribute("name", name);
        model.addAttribute("displayName", session.getAttribute("displayName"));
        model.addAttribute("displayImage", session.getAttribute("displayImage"));
        model.addAttribute("createProject", new Project());
        model.addAttribute("chipsets", chipsetService.getAllChipsets());
        if(projects == null || projects.isEmpty()){
            return "snapdragontech";
        }
        else{
            return "Project";
        }
    }

    @GetMapping("/dashboard/odm/{name}/projects/{projectId}")
    public String insideProject(
            @PathVariable String name,
            @PathVariable Long projectId,
            HttpSession session,
            Model model) {
        Project project = projectRepo.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        List<Folder> folders = folderRepo.findByProjectId(projectId);

        model.addAttribute("name", name);
        model.addAttribute("project", project);
        model.addAttribute("folders", folders);
        model.addAttribute("displayName", session.getAttribute("displayName"));
        model.addAttribute("displayImage", session.getAttribute("displayImage"));
        return "projectFolder";
    }


    @GetMapping("/dashboard/odm/{name}/projects/{projectId}/folders/{folderId}")
    public String folderFiles(
            @PathVariable String name,
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

        model.addAttribute("name", name);
        model.addAttribute("project", project);
        model.addAttribute("folder", folder);
        model.addAttribute("moveTargets", moveTargets);
        model.addAttribute("displayName", session.getAttribute("displayName"));
        return "adminFolderFiles";
    }

    @PostMapping("/dashboard/odm/{name}/projects/{projectId}/delete")
    public String deleteProject(
            @PathVariable String name,
            @PathVariable Long projectId) {
        projectRepo.deleteById(projectId);
        return "redirect:/dashboard/odm/" + name + "/";
    }

    @PostMapping("/dashboard/odm/{name}/projects/{projectId}/folders/{folderId}/delete")
    public String deleteFolder(
            @PathVariable String name,
            @PathVariable Long projectId,
            @PathVariable Long folderId) {
        folderRepo.deleteById(folderId);
        return "redirect:/dashboard/odm/" + name + "/projects/" + projectId;
    }

}
