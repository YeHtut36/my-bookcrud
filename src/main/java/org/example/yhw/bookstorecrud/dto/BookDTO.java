package org.example.yhw.bookstorecrud.dto;

import lombok.*;
import org.example.yhw.bookstorecrud.validation.PastOrPresentDate;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class BookDTO {
    private Long id;

    @NotBlank(message = "Title is required")
    private String title;

    @NotNull(message = "Author is required")
    private Long authorId;

    private String authorName;

    @NotNull(message = "Publication date is required")
    @PastOrPresentDate
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate publicationDate;

    private LocalDateTime lastUpdated;
}
