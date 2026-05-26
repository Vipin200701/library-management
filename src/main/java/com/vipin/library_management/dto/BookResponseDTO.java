package com.vipin.library_management.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class BookResponseDTO {

    private Long id;
    private String title;
    private String author;
    private String isbn;
    private String genre;
    private Integer publishedYear;
    private Integer totalCopies;
    private Integer availableCopies;
    private String status;           // "Available" or "Borrowed"
    private LocalDateTime createdAt;
}