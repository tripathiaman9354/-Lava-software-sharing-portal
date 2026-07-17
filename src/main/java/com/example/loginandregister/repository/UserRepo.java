package com.example.loginandregister.repository;

import com.example.loginandregister.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepo extends JpaRepository<User , Long> {

    User findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByPassword(String password);

    List<User> findByOdmName(String odmName);
}
