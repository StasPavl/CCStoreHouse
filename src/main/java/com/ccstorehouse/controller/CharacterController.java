package com.ccstorehouse.controller;

import com.ccstorehouse.model.Character;
import com.ccstorehouse.model.User;
import com.ccstorehouse.model.Family;
import com.ccstorehouse.repository.FamilyRepository;
import com.ccstorehouse.service.CharacterService;
import com.ccstorehouse.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/characters")
public class CharacterController {

    private final CharacterService characterService;
    private final UserService userService;
    private final FamilyRepository familyRepository;

    @Autowired
    public CharacterController(CharacterService characterService, UserService userService, FamilyRepository familyRepository) {
        this.characterService = characterService;
        this.userService = userService;
        this.familyRepository = familyRepository;
    }

    @GetMapping
    public String listCharacters(Model model, Principal principal) {
        User user = getCurrentUser(principal);
        if (user != null) {
            List<Character> characters = characterService.getAllCharactersByUser(user);
            model.addAttribute("characters", characters);
            return "characters/list";
        }
        return "redirect:/login";
    }

    @GetMapping("/new")
    public String newCharacterForm(Model model, Principal principal, @RequestParam(value = "familyId", required = false) Long familyId) {
        User user = getCurrentUser(principal);
        if (user != null) {
            Character character = new Character();
            if (familyId != null) {
                Optional<Family> familyOpt = familyRepository.findById(familyId);
                familyOpt.ifPresent(character::setFamily);
            }
            model.addAttribute("character", character);
            return "characters/form";
        }
        return "redirect:/login";
    }

    @PostMapping
    public String saveCharacter(@ModelAttribute Character character, Principal principal, @RequestParam(value = "familyId", required = false) Long familyId, RedirectAttributes redirectAttributes) {
        User user = getCurrentUser(principal);
        if (user != null) {
            character.setUser(user);
            if (familyId != null) {
                Optional<Family> familyOpt = familyRepository.findById(familyId);
                familyOpt.ifPresent(character::setFamily);
            }
            characterService.saveCharacter(character);
            redirectAttributes.addFlashAttribute("successMessage", "Character saved successfully!");
            return "redirect:/characters";
        }
        return "redirect:/login";
    }

    @GetMapping("/{id}")
    public String viewCharacter(@PathVariable Long id, Model model, Principal principal) {
        User user = getCurrentUser(principal);
        if (user != null) {
            Optional<Character> character = characterService.getCharacterById(id);
            if (character.isPresent() && character.get().getUser().getId().equals(user.getId())) {
                model.addAttribute("character", character.get());
                return "characters/view";
            }
            return "redirect:/characters";
        }
        return "redirect:/login";
    }

    @GetMapping("/{id}/edit")
    public String editCharacterForm(@PathVariable Long id, Model model, Principal principal) {
        User user = getCurrentUser(principal);
        if (user != null) {
            Optional<Character> character = characterService.getCharacterById(id);
            if (character.isPresent() && character.get().getUser().getId().equals(user.getId())) {
                model.addAttribute("character", character.get());
                return "characters/form";
            }
            return "redirect:/characters";
        }
        return "redirect:/login";
    }

    @GetMapping("/{id}/delete")
    public String deleteCharacter(@PathVariable Long id, Principal principal, RedirectAttributes redirectAttributes) {
        User user = getCurrentUser(principal);
        if (user != null) {
            Optional<Character> character = characterService.getCharacterById(id);
            if (character.isPresent() && character.get().getUser().getId().equals(user.getId())) {
                characterService.deleteCharacter(id);
                redirectAttributes.addFlashAttribute("successMessage", "Character deleted successfully!");
            }
            return "redirect:/characters";
        }
        return "redirect:/login";
    }

    @GetMapping("/search")
    public String searchCharacters(@RequestParam String query, Model model, Principal principal) {
        User user = getCurrentUser(principal);
        if (user != null) {
            List<Character> characters = characterService.searchCharacters(user, query);
            model.addAttribute("characters", characters);
            model.addAttribute("query", query);
            return "characters/list";
        }
        return "redirect:/login";
    }
    
    @GetMapping("/search/family")
    public String searchCharactersByFamily(@RequestParam String family, Model model, Principal principal) {
        User user = getCurrentUser(principal);
        if (user != null) {
            List<Character> characters = characterService.searchCharactersByFamily(user, family);
            model.addAttribute("characters", characters);
            model.addAttribute("familyQuery", family);
            return "characters/list";
        }
        return "redirect:/login";
    }

    private User getCurrentUser(Principal principal) {
        if (principal != null) {
            return userService.findUserByEmail(principal.getName()).orElse(null);
        }
        return null;
    }
} 