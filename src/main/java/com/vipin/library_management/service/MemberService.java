package com.vipin.library_management.service;

import com.vipin.library_management.dto.MemberRequestDTO;
import com.vipin.library_management.dto.MemberResponseDTO;
import com.vipin.library_management.entity.Member;
import com.vipin.library_management.exception.LibraryException;
import com.vipin.library_management.exception.ResourceNotFoundException;
import com.vipin.library_management.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    // ── Add new member ─────────────────────────────────
    public MemberResponseDTO addMember(MemberRequestDTO dto) {
        if (memberRepository.existsByEmail(dto.getEmail())) {
            throw new LibraryException("Member with email " + dto.getEmail() + " already exists");
        }

        Member member = Member.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .build();

        return toDTO(memberRepository.save(member));
    }

    // ── Get all members ────────────────────────────────
    public List<MemberResponseDTO> getAllMembers() {
        return memberRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // ── Get member by ID ───────────────────────────────
    public MemberResponseDTO getMemberById(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found with id: " + id));
        return toDTO(member);
    }

    // ── Search members by name ─────────────────────────
    public List<MemberResponseDTO> searchMembers(String name) {
        return memberRepository.findByNameContainingIgnoreCase(name)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // ── Get only active members ────────────────────────
    public List<MemberResponseDTO> getActiveMembers() {
        return memberRepository.findByIsActive(true)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // ── Update member ──────────────────────────────────
    public MemberResponseDTO updateMember(Long id, MemberRequestDTO dto) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found with id: " + id));

        // If email changed, check new email isn't taken
        if (!member.getEmail().equals(dto.getEmail()) && memberRepository.existsByEmail(dto.getEmail())) {
            throw new LibraryException("Email " + dto.getEmail() + " is already used by another member");
        }

        member.setName(dto.getName());
        member.setEmail(dto.getEmail());
        member.setPhone(dto.getPhone());

        return toDTO(memberRepository.save(member));
    }

    // ── Deactivate member (soft delete) ───────────────
    public MemberResponseDTO deactivateMember(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found with id: " + id));

        if (!member.getIsActive()) {
            throw new LibraryException("Member is already inactive");
        }

        member.setIsActive(false);
        return toDTO(memberRepository.save(member));
    }

    // ── Reactivate member ──────────────────────────────
    public MemberResponseDTO reactivateMember(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found with id: " + id));

        if (member.getIsActive()) {
            throw new LibraryException("Member is already active");
        }

        member.setIsActive(true);
        return toDTO(memberRepository.save(member));
    }

    // ── Hard delete ────────────────────────────────────
    public void deleteMember(Long id) {
        if (!memberRepository.existsById(id)) {
            throw new ResourceNotFoundException("Member not found with id: " + id);
        }
        memberRepository.deleteById(id);
    }

    // ── Entity → DTO mapper ────────────────────────────
    public MemberResponseDTO toDTO(Member member) {
        return MemberResponseDTO.builder()
                .id(member.getId())
                .name(member.getName())
                .email(member.getEmail())
                .phone(member.getPhone())
                .membershipDate(member.getMembershipDate())
                .isActive(member.getIsActive())
                .createdAt(member.getCreatedAt())
                .build();
    }
}