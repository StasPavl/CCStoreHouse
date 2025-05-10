package com.ccstorehouse.service;

import com.ccstorehouse.model.FamilyMember;
import com.ccstorehouse.model.User;
import com.ccstorehouse.repository.FamilyMemberRepository;
import com.ccstorehouse.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class FamilyMemberService {

    private final FamilyMemberRepository familyMemberRepository;
    private final UserRepository userRepository;

    @Autowired
    public FamilyMemberService(FamilyMemberRepository familyMemberRepository, UserRepository userRepository) {
        this.familyMemberRepository = familyMemberRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public FamilyMember addFamilyMember(User owner, String memberEmail) {
        // Check if trying to add self
        if (owner.getEmail().equals(memberEmail)) {
            throw new RuntimeException("Cannot add yourself as a family member");
        }

        // Check if already a family member
        if (familyMemberRepository.existsByOwnerAndMemberEmailAndActiveTrue(owner, memberEmail)) {
            throw new RuntimeException("User is already a family member");
        }

        // Create new family member record
        FamilyMember familyMember = new FamilyMember();
        familyMember.setOwner(owner);
        familyMember.setMemberEmail(memberEmail);

        // If the user already exists, set the member
        userRepository.findByEmail(memberEmail).ifPresent(familyMember::setMember);

        return familyMemberRepository.save(familyMember);
    }

    @Transactional
    public void removeFamilyMember(User owner, Long memberId) {
        User member = userRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + memberId));

        FamilyMember familyMember = familyMemberRepository.findByOwnerAndMemberAndActiveTrue(owner, member)
                .orElseThrow(() -> new RuntimeException("Family member relationship not found"));

        familyMember.setActive(false);
        familyMemberRepository.save(familyMember);
    }

    public List<FamilyMember> getFamilyMembers(User user) {
        return familyMemberRepository.findByOwnerAndActiveTrue(user);
    }

    public List<FamilyMember> getSharedWithMe(User user) {
        return familyMemberRepository.findByMemberAndActiveTrue(user);
    }

    public boolean isFamilyMember(User owner, User member) {
        return familyMemberRepository.existsByOwnerAndMemberAndActiveTrue(owner, member);
    }
} 