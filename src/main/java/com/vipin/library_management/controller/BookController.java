package com.vipin.library_management.controller;

import com.vipin.library_management.dto.ApiResponse;
import com.vipin.library_management.dto.BookRequestDTO;
import com.vipin.library_management.dto.BookResponseDTO;
import com.vipin.library_management.service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @PostMapping
    public ResponseEntity<ApiResponse<BookResponseDTO>> addBook(
            @Valid @RequestBody BookRequestDTO dto) {
        BookResponseDTO book = bookService.addBook(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Book added successfully", book));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<BookResponseDTO>>> getAllBooks() {
        return ResponseEntity.ok(
                ApiResponse.success("Books fetched successfully", bookService.getAllBooks()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BookResponseDTO>> getBookById(@PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.success("Book fetched successfully", bookService.getBookById(id)));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<BookResponseDTO>>> searchBooks(
            @RequestParam String keyword) {
        return ResponseEntity.ok(
                ApiResponse.success("Search results", bookService.searchBooks(keyword)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<BookResponseDTO>> updateBook(
            @PathVariable Long id, @Valid @RequestBody BookRequestDTO dto) {
        return ResponseEntity.ok(
                ApiResponse.success("Book updated successfully", bookService.updateBook(id, dto)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.ok(ApiResponse.success("Book deleted successfully", null));
    }
}