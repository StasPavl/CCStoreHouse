package com.ccstorehouse.service;

import com.ccstorehouse.model.Family;
import com.ccstorehouse.model.User;
import com.ccstorehouse.repository.FamilyRepository;
import com.ccstorehouse.repository.FamilyMemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FamilyService {

    private final FamilyRepository familyRepository;
    private final FamilyMemberRepository familyMemberRepository;

    @Autowired
    public FamilyService(FamilyRepository familyRepository, FamilyMemberRepository familyMemberRepository) {
        this.familyRepository = familyRepository;
        this.familyMemberRepository = familyMemberRepository;
    }

    @Transactional
    public Family saveFamily(Family family) {
        return familyRepository.save(family);
    }

    public List<Family> getAllFamiliesByUser(User user) {
        // Get families owned by the user
        List<Family> ownedFamilies = familyRepository.findByUser(user);
        
        // Get families shared with the user through family members
        List<Family> sharedFamilies = familyRepository.findByUserIn(
            familyMemberRepository.findByMemberAndActiveTrue(user)
                .stream()
                .map(fm -> fm.getOwner())
                .collect(Collectors.toList())
        );
        
        // Combine and return all families
        ownedFamilies.addAll(sharedFamilies);
        return ownedFamilies;
    }

    public Family getFamilyById(Long id) {
        return familyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Family not found with id: " + id));
    }

    @Transactional
    public void deleteFamily(Long id) {
        familyRepository.deleteById(id);
    }

    public List<Family> searchFamilies(User user, String query) {
        // Get families owned by the user
        List<Family> ownedFamilies = familyRepository.findByUserAndNameContainingIgnoreCase(user, query);
        
        // Get families shared with the user through family members
        List<Family> sharedFamilies = familyRepository.findByUserInAndNameContainingIgnoreCase(
            familyMemberRepository.findByMemberAndActiveTrue(user)
                .stream()
                .map(fm -> fm.getOwner())
                .collect(Collectors.toList()),
            query
        );
        
        // Combine and return all families
        ownedFamilies.addAll(sharedFamilies);
        return ownedFamilies;
    }
} 