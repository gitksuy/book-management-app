package com.interview.bookapi.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "books")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Book {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String title;
    
    @Column(nullable = false)
    private String author;
    
    @Column(length = 500)
    private String description;
    
    private String isbn;
    
    @Column(name = "category")
    private String category;
    
    private Integer pageCount;
    
    private String publisher;
    
    @Column(name = "published_date")
    private String publishedDate;
    
    @Column(name = "thumbnail_url")
    private String thumbnailUrl;
    
    @Column(name = "rating")
    private Double rating;
    
    private String language;
    
    @Column(name = "preview_link")
    private String previewLink;
    
    @Column(name = "info_link")
    private String infoLink;
    
    @Column(name = "is_ebook")
    private Boolean isEbook;
    
    @Column(name = "text_snippet", length = 1000)
    private String textSnippet;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}