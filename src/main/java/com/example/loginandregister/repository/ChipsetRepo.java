package com.example.loginandregister.repository;

import com.example.loginandregister.model.Chipset;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChipsetRepo extends JpaRepository <Chipset, Long> {
    boolean existsByName(String name);

    Chipset findByName(String name);

    void deleteByName(String name);

    List<Chipset> findByNameContainingIgnoreCase(String name);

}
