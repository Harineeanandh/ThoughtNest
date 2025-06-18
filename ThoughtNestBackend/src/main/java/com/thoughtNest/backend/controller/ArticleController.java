package com.thoughtNest.backend.controller;

import com.thoughtNest.backend.dto.ArticleDTO;
import com.thoughtNest.backend.model.Article;
import com.thoughtNest.backend.model.User;
import com.thoughtNest.backend.service.ArticleService;
import com.thoughtNest.backend.service.GCSUploadService;
import com.thoughtNest.backend.service.UserService;
import com.thoughtNest.backend.util.ResponseHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/articles")
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    @Autowired
    private UserService userService;

    @Autowired
    private GCSUploadService gcsUploadService;

    @Value("${app.upload.dir}")
    private String uploadDir;

    @PostMapping
    public ResponseEntity<?> createArticle(@RequestBody Article article, Authentication authentication) {
        String username = authentication.getName();
        Optional<User> userOpt = userService.findByEmail(username);
        if (userOpt.isEmpty()) {
            return ResponseHandler.generateResponse("User not found", HttpStatus.UNAUTHORIZED, null);
        }
        article.setAuthor(userOpt.get());
        article.setDate(LocalDate.now());
        article.setLastModifiedDate(LocalDateTime.now());

        Article savedArticle = articleService.saveArticle(article);
        ArticleDTO dto = new ArticleDTO(savedArticle);
        return ResponseHandler.generateResponse("Article created", HttpStatus.OK, dto);
    }

    @GetMapping("/my")
    public ResponseEntity<?> getMyArticles(Authentication authentication) {
        String username = authentication.getName();
        Optional<User> userOpt = userService.findByEmail(username);
        if (userOpt.isEmpty()) {
            return ResponseHandler.generateResponse("User not found", HttpStatus.UNAUTHORIZED, null);
        }

        List<Article> articles = articleService.getArticlesByUser(userOpt.get());
        List<ArticleDTO> dtos = articles.stream().map(ArticleDTO::new).toList();

        return ResponseHandler.generateResponse("User articles retrieved", HttpStatus.OK, dtos);
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllArticles() {
        List<Article> articles = articleService.getAllArticles();
        List<ArticleDTO> dtos = articles.stream().map(ArticleDTO::new).toList();

        return ResponseHandler.generateResponse("All articles retrieved", HttpStatus.OK, dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getArticle(@PathVariable Long id) {
        Optional<ArticleDTO> dtoOpt = articleService.getArticleDTOById(id);
        if (dtoOpt.isEmpty()) {
            return ResponseHandler.generateResponse("Article not found", HttpStatus.NOT_FOUND, null);
        }

        return ResponseHandler.generateResponse("Article retrieved", HttpStatus.OK, dtoOpt.get());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateArticle(@PathVariable Long id, @RequestBody Article updatedArticle, Authentication authentication) {
        Optional<Article> articleOpt = articleService.getArticleById(id);
        if (articleOpt.isEmpty()) {
            return ResponseHandler.generateResponse("Article not found", HttpStatus.NOT_FOUND, null);
        }

        Article article = articleOpt.get();
        String username = authentication.getName();

        if (!article.getAuthor().getEmail().equals(username)) {
            return ResponseHandler.generateResponse("Not authorized", HttpStatus.FORBIDDEN, null);
        }

        article.setTitle(updatedArticle.getTitle());
        article.setContent(updatedArticle.getContent());
        article.setImage(updatedArticle.getImage());
        article.setLastModifiedDate(LocalDateTime.now());

        Article savedArticle = articleService.saveArticle(article);
        ArticleDTO dto = new ArticleDTO(savedArticle);
        return ResponseHandler.generateResponse("Article updated", HttpStatus.OK, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteArticle(@PathVariable Long id, Authentication authentication) {
        Optional<Article> articleOpt = articleService.getArticleById(id);
        if (articleOpt.isEmpty()) {
            return ResponseHandler.generateResponse("Article not found", HttpStatus.NOT_FOUND, null);
        }

        Article article = articleOpt.get();
        String username = authentication.getName();

        if (!article.getAuthor().getEmail().equals(username)) {
            return ResponseHandler.generateResponse("Not authorized", HttpStatus.FORBIDDEN, null);
        }

        articleService.deleteArticle(id);
        return ResponseHandler.generateResponse("Article deleted", HttpStatus.OK, null);
    }

    @PostMapping("/upload-image")
    public ResponseEntity<?> uploadImage(@RequestParam("image") MultipartFile image) {
        try {
            if (image.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "No image uploaded"));
            }

            String imageUrl = gcsUploadService.uploadFile(
                    image.getOriginalFilename(),
                    image.getInputStream(),
                    image.getContentType()
            );

            return ResponseEntity.ok(Map.of("message", "Image uploaded successfully", "data", imageUrl));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "Image upload failed"));
        }
    }

    @PatchMapping("/{id}/publish")
    public ResponseEntity<?> togglePublishStatus(@PathVariable Long id,
                                                 @RequestParam("published") boolean published,
                                                 Authentication authentication) {
        Optional<Article> articleOpt = articleService.getArticleById(id);
        if (articleOpt.isEmpty()) {
            return ResponseHandler.generateResponse("Article not found", HttpStatus.NOT_FOUND, null);
        }

        Article article = articleOpt.get();
        String username = authentication.getName();

        if (!article.getAuthor().getEmail().equals(username)) {
            return ResponseHandler.generateResponse("Not authorized", HttpStatus.FORBIDDEN, null);
        }

        article.setPublished(published);
        article.setLastModifiedDate(LocalDateTime.now());
        articleService.saveArticle(article);

        return ResponseHandler.generateResponse(
                published ? "Article published" : "Article unpublished",
                HttpStatus.OK,
                null
        );
    }

    @GetMapping("/public")
    public ResponseEntity<?> getAllPublishedArticles() {
        List<Article> publishedArticles = articleService.getAllPublishedArticles();
        List<ArticleDTO> dtos = publishedArticles.stream().map(ArticleDTO::new).toList();

        return ResponseHandler.generateResponse("Published articles retrieved", HttpStatus.OK, dtos);
    }
}
