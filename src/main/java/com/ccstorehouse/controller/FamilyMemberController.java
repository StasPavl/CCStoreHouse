package com.ccstorehouse.controller;

import com.ccstorehouse.model.FamilyMember;
import com.ccstorehouse.model.User;
import com.ccstorehouse.service.FamilyMemberService;
import com.ccstorehouse.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/family-members")
public class FamilyMemberController {

    private final FamilyMemberService familyMemberService;
    private final UserService userService;

    @Autowired
    public FamilyMemberController(FamilyMemberService familyMemberService, UserService userService) {
        this.familyMemberService = familyMemberService;
        this.userService = userService;
    }

    @GetMapping
    public String listFamilyMembers(Model model, Principal principal) {
        User user = userService.findUserByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<FamilyMember> myFamilyMembers = familyMemberService.getFamilyMembers(user);
        List<FamilyMember> sharedWithMe = familyMemberService.getSharedWithMe(user);

        model.addAttribute("myFamilyMembers", myFamilyMembers);
        model.addAttribute("sharedWithMe", sharedWithMe);
        return "family-members/list";
    }

    @PostMapping("/add")
    public String addFamilyMember(@RequestParam String email, Principal principal, RedirectAttributes redirectAttributes) {
        try {
            User owner = userService.findUserByEmail(principal.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            familyMemberService.addFamilyMember(owner, email);
            redirectAttributes.addFlashAttribute("successMessage", "Family member added successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/family-members";
    }

    @PostMapping("/remove/{memberId}")
    public String removeFamilyMember(@PathVariable Long memberId, Principal principal, RedirectAttributes redirectAttributes) {
        try {
            User owner = userService.findUserByEmail(principal.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            familyMemberService.removeFamilyMember(owner, memberId);
            redirectAttributes.addFlashAttribute("successMessage", "Family member removed successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/family-members";
    }
} 