package org.example.bookapi.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BookRequest {
    @NotBlank
    private String title;
    @NotBlank
    private String author;
}
