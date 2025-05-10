package com.ccstorehouse.service;

import com.ccstorehouse.model.Character;
import com.ccstorehouse.model.User;
import com.ccstorehouse.repository.CharacterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CharacterService {

    private final CharacterRepository characterRepository;

    @Autowired
    public CharacterService(CharacterRepository characterRepository) {
        this.characterRepository = characterRepository;
    }

    public List<Character> getAllCharactersByUser(User user) {
        return characterRepository.findByUserOrderByNameAsc(user);
    }

    public Optional<Character> getCharacterById(Long id) {
        return characterRepository.findById(id);
    }

    public Character saveCharacter(Character character) {
        return characterRepository.save(character);
    }

    public void deleteCharacter(Long id) {
        characterRepository.deleteById(id);
    }

    public List<Character> searchCharacters(User user, String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return characterRepository.findByUserOrderByNameAsc(user);
        }
        return characterRepository.searchCharacters(user, searchTerm.trim());
    }
} 