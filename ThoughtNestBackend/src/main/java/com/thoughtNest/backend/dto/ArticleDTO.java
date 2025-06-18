package com.thoughtNest.backend.dto;

import com.thoughtNest.backend.model.Article;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Data Transfer Object (DTO) for exposing article data to the frontend.
 * This class ensures sensitive fields are not exposed directly and formats data cleanly.
 */
public class ArticleDTO {
    private Long id;
    private String title;
    private String content;
    private LocalDate date;
    private String image;
    private String authorUsername;
    private boolean published;
    private LocalDateTime lastModifiedDate;

    public ArticleDTO() {
        // Default constructor required for serialization/deserialization
    }

    /**
     * All-arguments constructor for manual mapping.
     */
    public ArticleDTO(Long id, String title, String content, LocalDate date,
                      String image, String authorUsername, boolean published,
                      LocalDateTime lastModifiedDate) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.date = date;
        this.image = image;
        this.authorUsername = authorUsername;
        this.published = published;
        this.lastModifiedDate = lastModifiedDate;
    }

    /**
     * Convenience constructor to convert from Article entity to DTO.
     * Safely handles null values for optional fields.
     */
    public ArticleDTO(Article article) {
        this.id = article.getId();
        this.title = article.getTitle();
        this.content = article.getContent();
        this.date = article.getDate();
        this.image = article.getImage();
        this.published = article.getPublished() != null ? article.getPublished() : false;
        this.authorUsername = article.getAuthor() != null ? article.getAuthor().getUsername() : "Unknown";
        this.lastModifiedDate = article.getLastModifiedDate();
    }

    // Getters and setters for all fields

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getAuthorUsername() {
        return authorUsername;
    }

    public void setAuthorUsername(String authorUsername) {
        this.authorUsername = authorUsername;
    }

    public boolean isPublished() {
        return published;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }

    public LocalDateTime getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(LocalDateTime lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }
}
