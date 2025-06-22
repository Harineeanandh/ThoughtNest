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
        if (imageFile != null && !imageFile.isEmpty()) {
            String imageUrl = gcsUploadService.uploadFile(
                    imageFile.getOriginalFilename(),
                    imageFile.getInputStream(),
                    imageFile.getContentType()
            );
            article.setImage(imageUrl); // Set the GCS image URL
        }

        article.setLastModifiedDate(LocalDateTime.now());
        return articleRepository.save(article);
    }

    public Article saveArticle(Article article) {
        article.setLastModifiedDate(LocalDateTime.now());
        return articleRepository.save(article);
    }
    @Transactional(readOnly = true)
    public Optional<Article> getArticleById(Long id) {
        return articleRepository.findById(id);
    }
    @Transactional(readOnly = true)
    public List<Article> getArticlesByUser(User user) {
        return articleRepository.findByAuthor(user);
    }

    public void deleteArticle(Long id) {
        articleRepository.deleteById(id);
    }
    @Transactional(readOnly = true)
    public List<Article> getAllArticles() {
        return articleRepository.findAll();
    }

    /**
     * Fetches ArticleDTO by ID with detailed logging for debugging DB issues.
     */
    @Transactional(readOnly = true)
    public Optional<ArticleDTO> getArticleDTOById(Long id) {
        try {
            System.out.println("📄 Attempting to fetch article DTO for ID: " + id);
            Optional<Article> articleOpt = articleRepository.findById(id);

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
            e.printStackTrace(); // <-- shows JDBC or Hibernate root cause
            throw new RuntimeException("Internal server error while fetching article", e);
        }
    }
    @Transactional(readOnly = true)
    public List<Article> getAllPublishedArticles() {
        return articleRepository.findByPublishedTrue();
    }
}
