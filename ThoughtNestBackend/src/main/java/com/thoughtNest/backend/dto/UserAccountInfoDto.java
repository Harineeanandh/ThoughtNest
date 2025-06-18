package com.thoughtNest.backend.dto;

/**
 * DTO to provide user account information and article statistics.
 */
public class UserAccountInfoDto {

    private String username;
    private String email;
    private int articleCount;
    private int publishedCount;

    // Constructor
    public UserAccountInfoDto(String username, String email, int articleCount, int publishedCount) {
        this.username = username;
        this.email = email;
        this.articleCount = articleCount;
        this.publishedCount = publishedCount;
    }

    // Getters and setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getArticleCount() {
        return articleCount;
    }

    public void setArticleCount(int articleCount) {
        this.articleCount = articleCount;
    }

    public int getPublishedCount() {
        return publishedCount;
    }

    public void setPublishedCount(int publishedCount) {
        this.publishedCount = publishedCount;
    }
}
