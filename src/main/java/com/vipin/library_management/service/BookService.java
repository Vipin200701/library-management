package com.vipin.library_management.service;

import com.vipin.library_management.dto.BookRequestDTO;
import com.vipin.library_management.dto.BookResponseDTO;
import com.vipin.library_management.entity.Book;
import com.vipin.library_management.exception.LibraryException;
import com.vipin.library_management.exception.ResourceNotFoundException;
import com.vipin.library_management.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;

    // ── Add new book ───────────────────────────────────
    public BookResponseDTO addBook(BookRequestDTO dto) {
        if (bookRepository.existsByIsbn(dto.getIsbn())) {
            throw new LibraryException("Book with ISBN " + dto.getIsbn() + " already exists");
        }

        Book book = Book.builder()
                .title(dto.getTitle())
                .author(dto.getAuthor())
                .isbn(dto.getIsbn())
                .genre(dto.getGenre())
                .publishedYear(dto.getPublishedYear())
                .totalCopies(dto.getTotalCopies())
                .availableCopies(dto.getTotalCopies())
                .build();

        return toDTO(bookRepository.save(book));
    }

    // ── Get all books ──────────────────────────────────
    public List<BookResponseDTO> getAllBooks() {
        return bookRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // ── Get book by ID ─────────────────────────────────
    public BookResponseDTO getBookById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id));
        return toDTO(book);
    }

    // ── Search books ───────────────────────────────────
    public List<BookResponseDTO> searchBooks(String keyword) {
        return bookRepository
                .findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(keyword, keyword)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // ── Update book ────────────────────────────────────
    public BookResponseDTO updateBook(Long id, BookRequestDTO dto) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id));

        // If ISBN changed, check new ISBN isn't taken
        if (!book.getIsbn().equals(dto.getIsbn()) && bookRepository.existsByIsbn(dto.getIsbn())) {
            throw new LibraryException("ISBN " + dto.getIsbn() + " is already used by another book");
        }

        int borrowedCopies = book.getTotalCopies() - book.getAvailableCopies();

        book.setTitle(dto.getTitle());
        book.setAuthor(dto.getAuthor());
        book.setIsbn(dto.getIsbn());
        book.setGenre(dto.getGenre());
        book.setPublishedYear(dto.getPublishedYear());
        book.setTotalCopies(dto.getTotalCopies());
        book.setAvailableCopies(dto.getTotalCopies() - borrowedCopies);

        return toDTO(bookRepository.save(book));
    }

    // ── Delete book ────────────────────────────────────
    public void deleteBook(Long id) {
        if (!bookRepository.existsById(id)) {
            throw new ResourceNotFoundException("Book not found with id: " + id);
        }
        bookRepository.deleteById(id);
    }

    // ── Entity → DTO mapper ────────────────────────────
    public BookResponseDTO toDTO(Book book) {
        return BookResponseDTO.builder()
                .id(book.getId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .isbn(book.getIsbn())
                .genre(book.getGenre())
                .publishedYear(book.getPublishedYear())
                .totalCopies(book.getTotalCopies())
                .availableCopies(book.getAvailableCopies())
                .status(book.getAvailableCopies() > 0 ? "Available" : "Borrowed")
                .createdAt(book.getCreatedAt())
                .build();
    }
}