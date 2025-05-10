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
    
    List<Character> findByUserOrderByFirstNameAsc(User user);
    
    @Query("SELECT c FROM Character c WHERE c.user = ?1 AND (LOWER(c.firstName) LIKE LOWER(CONCAT('%', ?2, '%')) OR LOWER(c.lastName) LIKE LOWER(CONCAT('%', ?2, '%')) OR LOWER(c.description) LIKE LOWER(CONCAT('%', ?2, '%')) OR LOWER(c.occupation) LIKE LOWER(CONCAT('%', ?2, '%')) OR (c.family IS NOT NULL AND LOWER(c.family.name) LIKE LOWER(CONCAT('%', ?2, '%'))))")
    List<Character> searchCharacters(User user, String searchTerm);
    
    List<Character> findByUserAndFamily_NameIgnoreCase(User user, String familyName);

    List<Character> findByUserIn(List<User> users);

    List<Character> findByUserAndFirstNameContainingIgnoreCase(User user, String firstName);
    List<Character> findByUserAndLastNameContainingIgnoreCase(User user, String lastName);
    List<Character> findByUserInAndFirstNameContainingIgnoreCase(List<User> users, String firstName);
    List<Character> findByUserInAndLastNameContainingIgnoreCase(List<User> users, String lastName);

    @Query("SELECT c FROM Character c WHERE c.user IN ?1 AND (LOWER(c.firstName) LIKE LOWER(CONCAT('%', ?2, '%')) OR LOWER(c.lastName) LIKE LOWER(CONCAT('%', ?2, '%')) OR LOWER(c.description) LIKE LOWER(CONCAT('%', ?2, '%')) OR LOWER(c.occupation) LIKE LOWER(CONCAT('%', ?2, '%')) OR (c.family IS NOT NULL AND LOWER(c.family.name) LIKE LOWER(CONCAT('%', ?2, '%'))))")
    List<Character> searchCharactersByUsers(List<User> users, String searchTerm);
} 