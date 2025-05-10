package com.ccstorehouse.controller;

import com.ccstorehouse.model.Character;
import com.ccstorehouse.model.User;
import com.ccstorehouse.model.Family;
import com.ccstorehouse.service.CharacterService;
import com.ccstorehouse.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

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
                if (character.getFamily() != null && character.getFamily().getName() != null && !character.getFamily().getName().trim().isEmpty()) {
                    String family = character.getFamily().getName().trim();
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
            // Find the family entity to get its ID
            List<Family> families = characterService.getFamilyRepository().findByUserAndNameContainingIgnoreCase(user, family);
            Family familyEntity = families.stream().filter(f -> f.getName().equalsIgnoreCase(family)).findFirst().orElse(null);
            Long familyId = familyEntity != null ? familyEntity.getId() : null;
            model.addAttribute("family", family);
            model.addAttribute("familyId", familyId);
            model.addAttribute("characters", familyCharacters);
            return "families/detail";
        }
        return "redirect:/login";
    }

    @GetMapping("/{family}/add-character")
    public String showAddCharacterToFamilyForm(@PathVariable String family, Model model, Principal principal) {
        User user = getCurrentUser(principal);
        if (user != null) {
            List<Character> unassignedCharacters = characterService.getUnassignedCharactersByUser(user);
            model.addAttribute("family", family);
            model.addAttribute("unassignedCharacters", unassignedCharacters);
            return "families/add-character";
        }
        return "redirect:/login";
    }

    @PostMapping("/{family}/add-character")
    public String addCharacterToFamily(@PathVariable String family, @RequestParam Long characterId, Principal principal, RedirectAttributes redirectAttributes) {
        User user = getCurrentUser(principal);
        try {
            if (user != null) {
                characterService.assignCharacterToFamily(user, characterId, family);
                redirectAttributes.addFlashAttribute("successMessage", "Character added to family successfully!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to add character to family: " + e.getMessage());
            String encodedFamily = URLEncoder.encode(family, StandardCharsets.UTF_8);
            return "redirect:/families/" + encodedFamily + "/add-character";
        }
        String encodedFamily = URLEncoder.encode(family, StandardCharsets.UTF_8);
        return "redirect:/families/" + encodedFamily;
    }

    @PostMapping("/{family}/delete")
    public String deleteFamily(@PathVariable String family, Principal principal, RedirectAttributes redirectAttributes) {
        User user = getCurrentUser(principal);
        if (user != null) {
            // Find the family entity
            List<Family> families = characterService.getFamilyRepository().findByUserAndNameContainingIgnoreCase(user, family);
            Family familyEntity = families.stream().filter(f -> f.getName().equalsIgnoreCase(family)).findFirst().orElse(null);
            if (familyEntity != null) {
                // Unassign all characters from this family
                characterService.unassignCharactersFromFamily(familyEntity);
                // Delete the family
                characterService.getFamilyRepository().delete(familyEntity);
                redirectAttributes.addFlashAttribute("successMessage", "Family deleted and characters unassigned.");
            }
        }
        return "redirect:/families";
    }

    private User getCurrentUser(Principal principal) {
        if (principal != null) {
            return userService.findUserByEmail(principal.getName()).orElse(null);
        }
        return null;
    }
} 