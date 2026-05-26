package com.vipin.library_management.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class MemberResponseDTO {

    private Long id;
    private String name;
    private String email;
    private String phone;
    private LocalDate membershipDate;
    private Boolean isActive;
    private LocalDateTime createdAt;
}