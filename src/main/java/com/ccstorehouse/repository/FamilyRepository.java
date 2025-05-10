package com.ccstorehouse.repository;

import com.ccstorehouse.model.Family;
import com.ccstorehouse.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FamilyRepository extends JpaRepository<Family, Long> {
    List<Family> findByUser(User user);
    List<Family> findByUserIn(List<User> users);
    List<Family> findByUserAndNameContainingIgnoreCase(User user, String name);
    List<Family> findByUserInAndNameContainingIgnoreCase(List<User> users, String name);
} 