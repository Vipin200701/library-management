package com.vipin.library_management.controller;

import com.vipin.library_management.dto.ApiResponse;
import com.vipin.library_management.dto.MemberRequestDTO;
import com.vipin.library_management.dto.MemberResponseDTO;
import com.vipin.library_management.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping
    public ResponseEntity<ApiResponse<MemberResponseDTO>> addMember(
            @Valid @RequestBody MemberRequestDTO dto) {
        MemberResponseDTO member = memberService.addMember(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Member registered successfully", member));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<MemberResponseDTO>>> getAllMembers() {
        return ResponseEntity.ok(
                ApiResponse.success("Members fetched successfully", memberService.getAllMembers()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MemberResponseDTO>> getMemberById(@PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.success("Member fetched successfully", memberService.getMemberById(id)));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<MemberResponseDTO>>> searchMembers(
            @RequestParam String name) {
        return ResponseEntity.ok(
                ApiResponse.success("Search results", memberService.searchMembers(name)));
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<MemberResponseDTO>>> getActiveMembers() {
        return ResponseEntity.ok(
                ApiResponse.success("Active members", memberService.getActiveMembers()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<MemberResponseDTO>> updateMember(
            @PathVariable Long id, @Valid @RequestBody MemberRequestDTO dto) {
        return ResponseEntity.ok(
                ApiResponse.success("Member updated successfully", memberService.updateMember(id, dto)));
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<ApiResponse<MemberResponseDTO>> deactivateMember(@PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.success("Member deactivated", memberService.deactivateMember(id)));
    }

    @PatchMapping("/{id}/reactivate")
    public ResponseEntity<ApiResponse<MemberResponseDTO>> reactivateMember(@PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.success("Member reactivated", memberService.reactivateMember(id)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteMember(@PathVariable Long id) {
        memberService.deleteMember(id);
        return ResponseEntity.ok(ApiResponse.success("Member deleted successfully", null));
    }
}