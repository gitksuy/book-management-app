package com.interview.bookapi.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class BookDTO {
    private Long id;
    private String isbn;
    private String title;
    private String author;
    private String description;
    private String publisher;
    private String publishedDate;
    private Integer pageCount;
    private String category;
    private Double rating;
    private String thumbnailUrl;
    private String language;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}