package com.example.loginandregister.model;

import jakarta.persistence.*;

import java.util.List;
@Entity
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String projectName;
    private String chipset;
    private String odmName;

    @ManyToOne
    @JoinColumn(name = "odm_id")
    private Odm odm;


    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Folder> folders;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getChipset() {
        return chipset;
    }

    public void setChipset(String chipset) {
        this.chipset = chipset;
    }

    public String getOdmName() {
        return odmName;
    }

    public void setOdmName(String odmName) {
        this.odmName = odmName;
    }

    public Odm getOdm() {
        return odm;
    }

    public void setOdm(Odm odm) {
        this.odm = odm;
    }

    public List<Folder> getFolders() {
        return folders;
    }

    public void setFolders(List<Folder> folders) {
        this.folders = folders;
    }
}
