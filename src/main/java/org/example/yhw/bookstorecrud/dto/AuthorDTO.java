package org.example.yhw.bookstorecrud.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class AuthorDTO {
    private Long id;

    @NotBlank(message = "Name is required")
    @Size(min = 2, message = "Name must be at least 2 characters long")
    private String name;

    private int booksCount;

}
