package com.example.loginandregister.model;


import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "folders")
public class Folder {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(nullable = false)
        private String name;

        // Project link
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "project_id")
        private Project project;



        @OneToMany(mappedBy = "folder", cascade = CascadeType.ALL)
        private List<FileEntity> files;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public List<FileEntity> getFiles() {
        return files;
    }

    public void setFiles(List<FileEntity> files) {
        this.files = files;
    }


    }

