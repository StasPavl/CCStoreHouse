package com.ccstorehouse.service;

import com.ccstorehouse.model.Character;
import com.ccstorehouse.model.User;
import com.ccstorehouse.repository.CharacterRepository;
import com.ccstorehouse.repository.FamilyMemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CharacterService {

    private final CharacterRepository characterRepository;
    private final FamilyMemberRepository familyMemberRepository;

    @Autowired
    public CharacterService(CharacterRepository characterRepository, FamilyMemberRepository familyMemberRepository) {
        this.characterRepository = characterRepository;
        this.familyMemberRepository = familyMemberRepository;
    }

    @Transactional
    public Character saveCharacter(Character character) {
        return characterRepository.save(character);
    }

    public List<Character> getAllCharactersByUser(User user) {
        // Get characters owned by the user
        List<Character> ownedCharacters = characterRepository.findByUser(user);
        
        // Get characters shared with the user through family members
        List<Character> sharedCharacters = characterRepository.findByUserIn(
            familyMemberRepository.findByMemberAndActiveTrue(user)
                .stream()
                .map(fm -> fm.getOwner())
                .collect(Collectors.toList())
        );
        
        // Combine and return all characters
        ownedCharacters.addAll(sharedCharacters);
        return ownedCharacters;
    }

    public Optional<Character> getCharacterById(Long id) {
        return characterRepository.findById(id);
    }

    @Transactional
    public void deleteCharacter(Long id) {
        characterRepository.deleteById(id);
    }

    public List<Character> searchCharacters(User user, String query) {
        // Get characters owned by the user
        List<Character> ownedCharacters = characterRepository.findByUserAndNameContainingIgnoreCase(user, query);
        
        // Get characters shared with the user through family members
        List<Character> sharedCharacters = characterRepository.findByUserInAndNameContainingIgnoreCase(
            familyMemberRepository.findByMemberAndActiveTrue(user)
                .stream()
                .map(fm -> fm.getOwner())
                .collect(Collectors.toList()),
            query
        );
        
        // Combine and return all characters
        ownedCharacters.addAll(sharedCharacters);
        return ownedCharacters;
    }

    public List<Character> searchCharactersByFamily(User user, String family) {
        if (family == null || family.trim().isEmpty()) {
            return characterRepository.findByUserOrderByNameAsc(user);
        }
        return characterRepository.findByUserAndFamilyContainingIgnoreCase(user, family.trim());
    }
} 