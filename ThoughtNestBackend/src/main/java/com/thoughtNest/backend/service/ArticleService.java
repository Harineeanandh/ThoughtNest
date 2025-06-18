package com.thoughtNest.backend.service;

import com.thoughtNest.backend.dto.ArticleDTO;
import com.thoughtNest.backend.model.Article;
import com.thoughtNest.backend.model.User;
import com.thoughtNest.backend.repository.ArticleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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

    /**
     * Saves an article and uploads image to GCS.
     * Automatically updates the last modified timestamp.
     *
     * @param article the article to save
     * @param imageFile the image file to upload (can be null)
     * @return the saved Article object
     * @throws IOException if file upload fails
     */
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

    public Optional<Article> getArticleById(Long id) {
        return articleRepository.findById(id);
    }

    public List<Article> getArticlesByUser(User user) {
        return articleRepository.findByAuthor(user);
    }

    public void deleteArticle(Long id) {
        articleRepository.deleteById(id);
    }

    public List<Article> getAllArticles() {
        return articleRepository.findAll();
    }

    public Optional<ArticleDTO> getArticleDTOById(Long id) {
        return articleRepository.findById(id).map(article -> new ArticleDTO(
                article.getId(),
                article.getTitle(),
                article.getContent(),
                article.getDate(),
                article.getImage(),
                article.getAuthor() != null ? article.getAuthor().getUsername() : null,
                article.getPublished() != null && article.getPublished(),
                article.getLastModifiedDate()
        ));
    }

    public List<Article> getAllPublishedArticles() {
        return articleRepository.findByPublishedTrue();
    }
}
