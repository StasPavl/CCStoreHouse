package com.ccstorehouse.controller;

import com.ccstorehouse.model.Character;
import com.ccstorehouse.model.User;
import com.ccstorehouse.service.CharacterService;
import com.ccstorehouse.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/families")
public class FamilyController {

    private final CharacterService characterService;
    private final UserService userService;

    @Autowired
    public FamilyController(CharacterService characterService, UserService userService) {
        this.characterService = characterService;
        this.userService = userService;
    }

    @GetMapping
    public String listFamilies(Model model, Principal principal) {
        User user = getCurrentUser(principal);
        if (user != null) {
            List<Character> allCharacters = characterService.getAllCharactersByUser(user);
            
            // Extract unique families and count characters in each
            Map<String, Integer> familyCounts = new HashMap<>();
            
            for (Character character : allCharacters) {
                if (character.getFamily() != null && !character.getFamily().trim().isEmpty()) {
                    String family = character.getFamily().trim();
                    familyCounts.put(family, familyCounts.getOrDefault(family, 0) + 1);
                }
            }
            
            // Sort families alphabetically
            List<Map.Entry<String, Integer>> sortedFamilies = new ArrayList<>(familyCounts.entrySet());
            sortedFamilies.sort(Map.Entry.comparingByKey());
            
            model.addAttribute("families", sortedFamilies);
            model.addAttribute("totalFamilies", sortedFamilies.size());
            return "families/list";
        }
        return "redirect:/login";
    }

    @GetMapping("/{family}")
    public String viewFamilyCharacters(@PathVariable String family, Model model, Principal principal) {
        User user = getCurrentUser(principal);
        if (user != null) {
            List<Character> familyCharacters = characterService.searchCharactersByFamily(user, family);
            model.addAttribute("family", family);
            model.addAttribute("characters", familyCharacters);
            return "families/detail";
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