package com.ccstorehouse.repository;

import com.ccstorehouse.model.FamilyMember;
import com.ccstorehouse.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FamilyMemberRepository extends JpaRepository<FamilyMember, Long> {
    List<FamilyMember> findByOwnerAndActiveTrue(User owner);
    List<FamilyMember> findByMemberAndActiveTrue(User member);
    Optional<FamilyMember> findByOwnerAndMemberAndActiveTrue(User owner, User member);
    boolean existsByOwnerAndMemberAndActiveTrue(User owner, User member);
    List<FamilyMember> findByMemberEmailAndActiveTrue(String email);
    boolean existsByOwnerAndMemberEmailAndActiveTrue(User owner, String email);
} 