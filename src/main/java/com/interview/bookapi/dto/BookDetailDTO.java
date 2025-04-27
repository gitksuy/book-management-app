package com.interview.bookapi.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class BookDetailDTO {
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
    private String previewLink;
    private String infoLink;
    private Boolean isEbook;
    private String textSnippet;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}