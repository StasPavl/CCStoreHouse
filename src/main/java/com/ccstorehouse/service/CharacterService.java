package com.ccstorehouse.service;

import com.ccstorehouse.model.Character;
import com.ccstorehouse.model.User;
import com.ccstorehouse.model.Family;
import com.ccstorehouse.repository.CharacterRepository;
import com.ccstorehouse.repository.FamilyMemberRepository;
import com.ccstorehouse.repository.FamilyRepository;
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
    private final FamilyRepository familyRepository;

    @Autowired
    public CharacterService(CharacterRepository characterRepository, FamilyMemberRepository familyMemberRepository, FamilyRepository familyRepository) {
        this.characterRepository = characterRepository;
        this.familyMemberRepository = familyMemberRepository;
        this.familyRepository = familyRepository;
    }

    @Transactional
    public Character saveCharacter(Character character) {
        // Only auto-assign family by last name if not already set
        if (character.getFamily() == null && character.getLastName() != null && character.getUser() != null) {
            String lastName = character.getLastName().trim();
            Family family = familyRepository.findByUserAndNameContainingIgnoreCase(character.getUser(), lastName)
                .stream()
                .filter(f -> f.getName().equalsIgnoreCase(lastName))
                .findFirst()
                .orElseGet(() -> {
                    Family newFamily = new Family();
                    newFamily.setName(lastName);
                    newFamily.setUser(character.getUser());
                    return familyRepository.save(newFamily);
                });
            character.setFamily(family);
        }
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
        
        // Combine all characters
        ownedCharacters.addAll(sharedCharacters);
        // Sort by first name
        ownedCharacters.sort((c1, c2) -> {
            if (c1.getFirstName() == null) return -1;
            if (c2.getFirstName() == null) return 1;
            return c1.getFirstName().compareToIgnoreCase(c2.getFirstName());
        });
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
        List<Character> ownedCharacters = characterRepository.searchCharacters(user, query);
        
        // Get characters shared with the user through family members
        List<User> sharedOwners = familyMemberRepository.findByMemberAndActiveTrue(user)
            .stream()
            .map(fm -> fm.getOwner())
            .collect(Collectors.toList());
        List<Character> sharedCharacters = sharedOwners.isEmpty() ? List.of() : characterRepository.searchCharactersByUsers(sharedOwners, query);
        
        // Combine and return all characters
        ownedCharacters.addAll(sharedCharacters);
        return ownedCharacters;
    }

    public List<Character> searchCharactersByFamily(User user, String family) {
        if (family == null || family.trim().isEmpty()) {
            return characterRepository.findByUserOrderByFirstNameAsc(user);
        }
        return characterRepository.findByUserAndFamily_NameIgnoreCase(user, family.trim());
    }

    public List<Character> getUnassignedCharactersByUser(User user) {
        return characterRepository.findByUser(user).stream()
            .filter(c -> c.getFamily() == null)
            .collect(Collectors.toList());
    }

    @Transactional
    public void assignCharacterToFamily(User user, Long characterId, String familyName) {
        Optional<Character> characterOpt = characterRepository.findById(characterId);
        if (characterOpt.isPresent()) {
            Character character = characterOpt.get();
            if (character.getUser().getId().equals(user.getId())) {
                Family family = familyRepository.findByUserAndNameContainingIgnoreCase(user, familyName)
                    .stream()
                    .filter(f -> f.getName().equalsIgnoreCase(familyName))
                    .findFirst()
                    .orElseGet(() -> {
                        Family newFamily = new Family();
                        newFamily.setName(familyName);
                        newFamily.setUser(user);
                        return familyRepository.save(newFamily);
                    });
                character.setFamily(family);
                characterRepository.save(character);
            }
        }
    }

    public FamilyRepository getFamilyRepository() {
        return familyRepository;
    }

    @Transactional
    public void unassignCharactersFromFamily(Family family) {
        List<Character> characters = characterRepository.findByUser(family.getUser()).stream()
            .filter(c -> c.getFamily() != null && c.getFamily().getId().equals(family.getId()))
            .collect(Collectors.toList());
        for (Character character : characters) {
            character.setFamily(null);
            characterRepository.save(character);
        }
    }
} 