package com.example.loginandregister.repository;

import com.example.loginandregister.model.FileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileEntityRepo extends JpaRepository<FileEntity, Long> {
}

