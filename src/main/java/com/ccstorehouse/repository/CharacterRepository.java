package com.ccstorehouse.repository;

import com.ccstorehouse.model.Character;
import com.ccstorehouse.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CharacterRepository extends JpaRepository<Character, Long> {
    
    List<Character> findByUser(User user);
    
    List<Character> findByUserOrderByNameAsc(User user);
    
    @Query("SELECT c FROM Character c WHERE c.user = ?1 AND " +
           "(LOWER(c.name) LIKE LOWER(CONCAT('%', ?2, '%')) OR " +
           "LOWER(c.description) LIKE LOWER(CONCAT('%', ?2, '%')) OR " +
           "LOWER(c.occupation) LIKE LOWER(CONCAT('%', ?2, '%')) OR " +
           "LOWER(c.family) LIKE LOWER(CONCAT('%', ?2, '%')))")
    List<Character> searchCharacters(User user, String searchTerm);
    
    List<Character> findByUserAndFamilyContainingIgnoreCase(User user, String family);

    List<Character> findByUserIn(List<User> users);

    List<Character> findByUserAndNameContainingIgnoreCase(User user, String name);

    List<Character> findByUserInAndNameContainingIgnoreCase(List<User> users, String name);
} 