package com.example.loginandregister.repository;

import com.example.loginandregister.model.Odm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface OdmRepo extends JpaRepository<Odm, Long> {
    boolean existsByName(String name);

    Odm findByEmail(String email);

    Odm findByName(String name);

    void deleteByName(String name);

    List<Odm> findByNameContainingIgnoreCase(String name);


}

