package com.vipin.library_management.repository;

import com.vipin.library_management.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    Optional<Book> findByIsbn(String isbn);

    boolean existsByIsbn(String isbn);

    // Search by title or author (case-insensitive)
    List<Book> findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(
            String title, String author);

    // Only books that have copies available
    List<Book> findByAvailableCopiesGreaterThan(Integer copies);

    // Books by genre
    List<Book> findByGenreIgnoreCase(String genre);
}