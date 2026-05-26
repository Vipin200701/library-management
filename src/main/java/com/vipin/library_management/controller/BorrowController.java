package com.vipin.library_management.controller;

import com.vipin.library_management.dto.ApiResponse;
import com.vipin.library_management.dto.BorrowRequestDTO;
import com.vipin.library_management.dto.BorrowResponseDTO;
import com.vipin.library_management.service.BorrowService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/borrow")
@RequiredArgsConstructor
public class BorrowController {

    private final BorrowService borrowService;

    // Issue a book to a member
    @PostMapping("/issue")
    public ResponseEntity<ApiResponse<BorrowResponseDTO>> borrowBook(
            @Valid @RequestBody BorrowRequestDTO dto) {
        BorrowResponseDTO record = borrowService.borrowBook(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Book issued successfully", record));
    }

    // Return a book
    @PatchMapping("/return/{borrowRecordId}")
    public ResponseEntity<ApiResponse<BorrowResponseDTO>> returnBook(
            @PathVariable Long borrowRecordId) {
        return ResponseEntity.ok(
                ApiResponse.success("Book returned successfully",
                        borrowService.returnBook(borrowRecordId)));
    }

    // All currently borrowed books
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<BorrowResponseDTO>>> getAllBorrowed() {
        return ResponseEntity.ok(
                ApiResponse.success("Active borrows fetched",
                        borrowService.getAllBorrowedBooks()));
    }

    // All overdue books
    @GetMapping("/overdue")
    public ResponseEntity<ApiResponse<List<BorrowResponseDTO>>> getOverdue() {
        return ResponseEntity.ok(
                ApiResponse.success("Overdue records fetched",
                        borrowService.getOverdueBooks()));
    }

    // Borrow history for a specific member
    @GetMapping("/member/{memberId}/history")
    public ResponseEntity<ApiResponse<List<BorrowResponseDTO>>> getMemberHistory(
            @PathVariable Long memberId) {
        return ResponseEntity.ok(
                ApiResponse.success("Member borrow history",
                        borrowService.getMemberHistory(memberId)));
    }

    // Borrow history for a specific book
    @GetMapping("/book/{bookId}/history")
    public ResponseEntity<ApiResponse<List<BorrowResponseDTO>>> getBookHistory(
            @PathVariable Long bookId) {
        return ResponseEntity.ok(
                ApiResponse.success("Book borrow history",
                        borrowService.getBookHistory(bookId)));
    }

    // Get single borrow record
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BorrowResponseDTO>> getBorrowRecord(
            @PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.success("Borrow record fetched",
                        borrowService.getBorrowRecord(id)));
    }

    // Manually trigger overdue marking
    @PatchMapping("/mark-overdue")
    public ResponseEntity<ApiResponse<String>> markOverdue() {
        int count = borrowService.markOverdueRecords();
        return ResponseEntity.ok(
                ApiResponse.success(count + " records marked as overdue", null));
    }
}