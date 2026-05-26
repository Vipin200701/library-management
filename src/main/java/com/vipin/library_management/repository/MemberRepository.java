package com.vipin.library_management.repository;

import com.vipin.library_management.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEmail(String email);

    boolean existsByEmail(String email);

    // All active members
    List<Member> findByIsActive(Boolean isActive);

    // Search by name
    List<Member> findByNameContainingIgnoreCase(String name);
}