package com.ccstorehouse.controller;

import com.ccstorehouse.model.User;
import com.ccstorehouse.service.CharacterService;
import com.ccstorehouse.service.UserService;
import com.ccstorehouse.service.FamilyMemberService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    private static final Logger logger = LoggerFactory.getLogger(ProfileController.class);
    
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final CharacterService characterService;
    private final FamilyMemberService familyMemberService;

    @Autowired
    public ProfileController(UserService userService, PasswordEncoder passwordEncoder, 
                           CharacterService characterService, FamilyMemberService familyMemberService) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.characterService = characterService;
        this.familyMemberService = familyMemberService;
    }

    @GetMapping
    public String showProfile(Model model, Principal principal) {
        if (principal != null) {
            User user = userService.findUserByEmail(principal.getName()).orElse(null);
            if (user != null) {
                model.addAttribute("user", user);
                model.addAttribute("characterCount", characterService.getAllCharactersByUser(user).size());
                model.addAttribute("myFamilyMembers", familyMemberService.getFamilyMembers(user));
                return "profile/view";
            }
        }
        return "redirect:/login";
    }

    @GetMapping("/edit")
    public String editProfileForm(Model model, Principal principal) {
        logger.info("Accessing profile edit form");
        if (principal != null) {
            User user = userService.findUserByEmail(principal.getName()).orElse(null);
            if (user != null) {
                model.addAttribute("user", user);
                model.addAttribute("userForm", new UserProfileForm(user.getFirstName(), user.getLastName(), user.getEmail(), null));
                return "profile/edit";
            }
        }
        return "redirect:/login";
    }

    @GetMapping("/change-password")
    public String changePasswordRedirect() {
        return "redirect:/profile/edit";
    }

    @PostMapping("/edit")
    public String updateProfile(@ModelAttribute("userForm") UserProfileForm userForm, 
                               Principal principal, 
                               RedirectAttributes redirectAttributes) {
        logger.info("Processing profile update form");
        try {
            if (principal != null) {
                User user = userService.findUserByEmail(principal.getName()).orElse(null);
                if (user != null) {
                    // Update user information
                    user.setFirstName(userForm.getFirstName());
                    user.setLastName(userForm.getLastName());
                    
                    // Handle password change if provided
                    if (userForm.getNewPassword() != null && !userForm.getNewPassword().isEmpty()) {
                        user.setPassword(passwordEncoder.encode(userForm.getNewPassword()));
                    }
                    
                    userService.updateUser(user);
                    redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully!");
                    return "redirect:/profile";
                }
            }
            return "redirect:/login";
        } catch (Exception e) {
            logger.error("Error updating profile: " + e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating profile: " + e.getMessage());
            return "redirect:/profile/edit";
        }
    }

    @PostMapping("/family/add")
    public String addFamilyMember(@RequestParam String email, Principal principal, RedirectAttributes redirectAttributes) {
        if (principal != null) {
            User user = userService.findUserByEmail(principal.getName()).orElse(null);
            if (user != null) {
                try {
                    familyMemberService.addFamilyMember(user, email);
                    redirectAttributes.addFlashAttribute("successMessage", "Family member added successfully!");
                } catch (Exception e) {
                    redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
                }
            }
        }
        return "redirect:/profile";
    }
    
    // Helper class for handling profile update form
    public static class UserProfileForm {
        private String firstName;
        private String lastName;
        private String email;
        private String newPassword;
        
        public UserProfileForm() {
        }
        
        public UserProfileForm(String firstName, String lastName, String email, String newPassword) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.email = email;
            this.newPassword = newPassword;
        }
        
        public String getFirstName() {
            return firstName;
        }
        
        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }
        
        public String getLastName() {
            return lastName;
        }
        
        public void setLastName(String lastName) {
            this.lastName = lastName;
        }
        
        public String getEmail() {
            return email;
        }
        
        public void setEmail(String email) {
            this.email = email;
        }
        
        public String getNewPassword() {
            return newPassword;
        }
        
        public void setNewPassword(String newPassword) {
            this.newPassword = newPassword;
        }
    }
} 