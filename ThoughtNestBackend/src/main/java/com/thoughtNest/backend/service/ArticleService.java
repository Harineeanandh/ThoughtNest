package com.thoughtNest.backend.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.thoughtNest.backend.dto.ArticleDTO;
import com.thoughtNest.backend.model.Article;
import com.thoughtNest.backend.model.User;
import com.thoughtNest.backend.repository.ArticleRepository;

/**
 * Service class for managing article-related operations.
 * Acts as an intermediary between the controller and the repository layer.
 */
@Service
public class ArticleService {

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private GCSUploadService gcsUploadService;

    public Article saveArticleWithImage(Article article, MultipartFile imageFile) throws IOException {
        try {
            System.out.println("💾 Saving article with image. Title: " + article.getTitle());

            if (imageFile != null && !imageFile.isEmpty()) {
                System.out.println("🖼️ Uploading image to GCS: " + imageFile.getOriginalFilename());
                String imageUrl = gcsUploadService.uploadFile(
                        imageFile.getOriginalFilename(),
                        imageFile.getInputStream(),
                        imageFile.getContentType()
                );
                article.setImage(imageUrl);
                System.out.println("✅ Image uploaded successfully: " + imageUrl);
            } else {
                System.out.println("ℹ️ No image uploaded. Skipping GCS upload.");
            }

            article.setLastModifiedDate(LocalDateTime.now());
            Article saved = articleRepository.save(article);
            System.out.println("✅ Article saved with ID: " + saved.getId());
            return saved;
        } catch (IOException e) {
            System.err.println("❌ IOException while uploading image or saving article: " + e.getMessage());
            throw e;
        }
    }

    public Article saveArticle(Article article) {
        System.out.println("💾 Saving article without image. Title: " + article.getTitle());
        article.setLastModifiedDate(LocalDateTime.now());
        Article saved = articleRepository.save(article);
        System.out.println("✅ Article saved with ID: " + saved.getId());
        return saved;
    }

    @Transactional(readOnly = true)
    public Optional<Article> getArticleById(Long id) {
        System.out.println("🔍 Fetching article by ID with author: " + id);
        Optional<Article> article = articleRepository.findByIdWithAuthor(id);
        if (article.isPresent()) {
            System.out.println("✅ Article found: " + article.get().getTitle() +
                    " | Author: " + article.get().getAuthor().getUsername());
        } else {
            System.err.println("⚠️ Article not found for ID: " + id);
        }
        return article;
    }

    @Transactional(readOnly = true)
    public List<Article> getArticlesByUser(User user) {
        System.out.println("📥 Fetching articles for user: " + user.getEmail());
        List<Article> articles = articleRepository.findByAuthor(user);
        System.out.println("📦 Total articles found: " + articles.size());
        return articles;
    }

    public void deleteArticle(Long id) {
        System.out.println("🗑️ Deleting article by ID: " + id);
        articleRepository.deleteById(id);
        System.out.println("✅ Article deleted.");
    }

    @Transactional(readOnly = true)
    public List<Article> getAllArticles() {
        System.out.println("🌍 Fetching all articles from database.");
        List<Article> articles = articleRepository.findAll();
        System.out.println("📦 Total articles found: " + articles.size());
        return articles;
    }

    /**
     * Fetches ArticleDTO by ID with detailed logging for debugging DB issues.
     */
    @Transactional(readOnly = true)
    public Optional<ArticleDTO> getArticleDTOById(Long id) {
        try {
            System.out.println("📄 Attempting to fetch article DTO for ID: " + id);
            Optional<Article> articleOpt = articleRepository.findByIdWithAuthor(id);

            if (articleOpt.isEmpty()) {
                System.err.println("⚠️ Article with ID " + id + " not found in database.");
                return Optional.empty();
            }

            Article article = articleOpt.get();
            System.out.println("✅ Article fetched successfully: " + article.getTitle());

            ArticleDTO dto = new ArticleDTO(
                    article.getId(),
                    article.getTitle(),
                    article.getContent(),
                    article.getDate(),
                    article.getImage(),
                    article.getAuthor() != null ? article.getAuthor().getUsername() : null,
                    article.getPublished() != null && article.getPublished(),
                    article.getLastModifiedDate()
            );

            System.out.println("📝 ArticleDTO constructed successfully.");
            return Optional.of(dto);

        } catch (Exception e) {
            System.err.println("❌ Exception while fetching article by ID: " + id);
            System.err.println("Message: " + e.getMessage());
            e.printStackTrace(); // shows JDBC or Hibernate root cause
            throw new RuntimeException("Internal server error while fetching article", e);
        }
    }

    @Transactional(readOnly = true)
    public List<Article> getAllPublishedArticles() {
        System.out.println("📢 Fetching all published articles.");
        List<Article> publishedArticles = articleRepository.findByPublishedTrue();
        System.out.println("📦 Published articles found: " + publishedArticles.size());
        return publishedArticles;
    }
}
