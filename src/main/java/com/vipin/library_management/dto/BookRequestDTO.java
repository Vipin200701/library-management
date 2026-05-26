package com.vipin.library_management.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class BookRequestDTO {

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Author is required")
    private String author;

    @NotBlank(message = "ISBN is required")
    private String isbn;

    private String genre;

    private Integer publishedYear;

    @Min(value = 1, message = "Total copies must be at least 1")
    @NotNull(message = "Total copies is required")
    private Integer totalCopies;
}