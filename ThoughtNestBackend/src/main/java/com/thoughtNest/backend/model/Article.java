package com.thoughtNest.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Represents a blog article in the ThoughtNest application.
 * Each article is authored by a user and may include a title, content,
 * optional image, publication status, and timestamps for creation and last update.
 */
@Entity
@Table(name = "articles")
public class Article {

    /**
     * Primary key for the article.
     * Auto-generated using IDENTITY strategy.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The title of the article.
     * Cannot be blank.
     */
    @NotBlank
    private String title;

    /**
     * The main content of the article.
     * Stored as TEXT in the database and cannot be blank.
     */
    @Column(columnDefinition = "TEXT")
    @NotBlank
    private String content;

    /**
     * The creation date of the article.
     * Typically set when the article is first created.
     */
    private LocalDate date;

    /**
     * The image URL or base64-encoded image data associated with the article.
     * Stored as TEXT in the database.
     */
    @Column(columnDefinition = "TEXT")
    private String image;

    /**
     * The author of the article.
     * Maps to a User entity with lazy loading.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User author;

    /**
     * Indicates whether the article is published.
     */
    private Boolean published;

    /**
     * The timestamp of the last modification to the article.
     * Useful for tracking edits and displaying update times.
     */
    private LocalDateTime lastModifiedDate;

    /**
     * Default constructor.
     */
    public Article() {}

    // Getters and setters

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }

    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }

    public void setContent(String content) { this.content = content; }

    public LocalDate getDate() { return date; }

    public void setDate(LocalDate date) { this.date = date; }

    public String getImage() { return image; }

    public void setImage(String image) { this.image = image; }

    public User getAuthor() { return author; }

    public void setAuthor(User author) { this.author = author; }

    public Boolean getPublished() { return published; }

    public void setPublished(Boolean published) { this.published = published; }

    public LocalDateTime getLastModifiedDate() { return lastModifiedDate; }

    public void setLastModifiedDate(LocalDateTime lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }
}
