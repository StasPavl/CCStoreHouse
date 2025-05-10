package com.ccstorehouse.controller;

import com.ccstorehouse.model.User;
import com.ccstorehouse.service.CharacterService;
import com.ccstorehouse.service.FamilyService;
import com.ccstorehouse.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {

    private final UserService userService;
    private final CharacterService characterService;
    private final FamilyService familyService;

    @Autowired
    public DashboardController(UserService userService, CharacterService characterService, FamilyService familyService) {
        this.userService = userService;
        this.characterService = characterService;
        this.familyService = familyService;
    }

    @GetMapping
    public String dashboard(Model model, Principal principal) {
        if (principal != null) {
            User user = userService.findUserByEmail(principal.getName()).orElse(null);
            if (user != null) {
                model.addAttribute("user", user);
                model.addAttribute("characterCount", characterService.getAllCharactersByUser(user).size());
                model.addAttribute("families", familyService.getAllFamiliesByUser(user));
                model.addAttribute("familyCount", familyService.getAllFamiliesByUser(user).size());
                return "dashboard";
            }
        }
        return "redirect:/login";
    }
} 