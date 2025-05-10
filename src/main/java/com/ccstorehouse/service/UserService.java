package com.ccstorehouse.service;

import com.ccstorehouse.model.User;
import com.ccstorehouse.repository.UserRepository;
import com.ccstorehouse.repository.FamilyMemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final FamilyMemberRepository familyMemberRepository;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, FamilyMemberRepository familyMemberRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.familyMemberRepository = familyMemberRepository;
    }

    @Transactional
    public User registerUser(User user) {
        // Check if email already exists
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("Email already registered");
        }

        // Encode password
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        // Save the user
        User savedUser = userRepository.save(user);

        // Check if this email was added as a family member by someone else
        familyMemberRepository.findByMemberEmailAndActiveTrue(user.getEmail())
            .forEach(familyMember -> {
                // Update the family member record with the new user
                familyMember.setMember(savedUser);
                familyMemberRepository.save(familyMember);
            });

        return savedUser;
    }

    public Optional<User> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }
    
    public void upgradeToPremium(User user) {
        user.setPremium(true);
        userRepository.save(user);
    }
    
    @Transactional
    public User updateUser(User user) {
        User existingUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Update user information
        existingUser.setFirstName(user.getFirstName());
        existingUser.setLastName(user.getLastName());
        
        // Only update password if a new one is provided
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        return userRepository.save(existingUser);
    }
} 