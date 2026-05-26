package com.vipin.library_management.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class MemberRequestDTO {

    @NotBlank(message = "Name is required")
    private String name;

    @Email(message = "Enter a valid email")
    @NotBlank(message = "Email is required")
    private String email;

    private String phone;
}