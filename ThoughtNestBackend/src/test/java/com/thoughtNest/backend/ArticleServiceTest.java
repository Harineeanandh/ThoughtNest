package com.thoughtNest.backend;

import com.thoughtNest.backend.dto.ArticleDTO;
import com.thoughtNest.backend.model.Article;
import com.thoughtNest.backend.model.User;
import com.thoughtNest.backend.repository.ArticleRepository;
import com.thoughtNest.backend.service.ArticleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit test class for ArticleService.
 *
 * This class focuses on testing business logic in the ArticleService class
 * by mocking its dependency (ArticleRepository). It verifies the service
 * methods behave correctly under various scenarios.
 */
class ArticleServiceTest {

    // Mock the ArticleRepository to isolate the service layer from the database
    @Mock
    private ArticleRepository articleRepository;

    // Inject the mock repository into the service
    @InjectMocks
    private ArticleService articleService;

    // Shared test data
    private Article article;
    private User user;

    /**
     * Initialize mock objects and setup test data before each test.
     */
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Initialize @Mock and @InjectMocks

        // Create a sample user
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        // Create a sample article authored by the user
        article = new Article();
        article.setId(1L);
        article.setAuthor(user);
        article.setTitle("Test Title");
        article.setContent("Test Content");
        article.setDate(LocalDate.now());
        article.setImage("test.jpg");
        article.setPublished(true);
    }

    /**
     * Test saving an article.
     */
    @Test
    void testSaveArticle() {
        // Simulate repository saving and returning the article
        when(articleRepository.save(article)).thenReturn(article);

        Article saved = articleService.saveArticle(article);

        assertEquals(article, saved); // Assert the saved article matches the original
        verify(articleRepository).save(article); // Verify save was called exactly once
    }

    /**
     * Test retrieving an article by its ID when found.
     */
    @Test
    void testGetArticleByIdFound() {
        when(articleRepository.findById(1L)).thenReturn(Optional.of(article));

        Optional<Article> found = articleService.getArticleById(1L);

        assertTrue(found.isPresent());
        assertEquals(article, found.get());
    }

    /**
     * Test retrieving an article by its ID when not found.
     */
    @Test
    void testGetArticleByIdNotFound() {
        when(articleRepository.findById(2L)).thenReturn(Optional.empty());

        Optional<Article> found = articleService.getArticleById(2L);

        assertFalse(found.isPresent());
    }

    /**
     * Test retrieving all articles by a specific user.
     */
    @Test
    void testGetArticlesByUser() {
        List<Article> articles = List.of(article);
        when(articleRepository.findByAuthor(user)).thenReturn(articles);

        List<Article> result = articleService.getArticlesByUser(user);

        assertEquals(1, result.size());
        assertEquals(article, result.get(0));
    }

    /**
     * Test deleting an article by its ID.
     */
    @Test
    void testDeleteArticle() {
        doNothing().when(articleRepository).deleteById(1L);

        articleService.deleteArticle(1L);

        verify(articleRepository).deleteById(1L); // Ensure deletion method is called
    }

    /**
     * Test retrieving all articles from the repository.
     */
    @Test
    void testGetAllArticles() {
        List<Article> articles = List.of(article);
        when(articleRepository.findAll()).thenReturn(articles);

        List<Article> result = articleService.getAllArticles();

        assertEquals(1, result.size());
        assertEquals(article, result.get(0));
    }

    /**
     * Test converting a found Article into an ArticleDTO.
     */
    @Test
    void testGetArticleDTOByIdFound() {
        when(articleRepository.findById(1L)).thenReturn(Optional.of(article));

        Optional<ArticleDTO> dtoOpt = articleService.getArticleDTOById(1L);

        assertTrue(dtoOpt.isPresent());

        ArticleDTO dto = dtoOpt.get();
        assertEquals(article.getId(), dto.getId());
        assertEquals(article.getTitle(), dto.getTitle());
        assertEquals(article.getContent(), dto.getContent());
        assertEquals(article.getDate(), dto.getDate());
        assertEquals(article.getImage(), dto.getImage());
        assertEquals(article.getAuthor().getUsername(), dto.getAuthorUsername());
        assertTrue(dto.isPublished());
    }

    /**
     * Test converting a non-existent article to DTO (should return empty).
     */
    @Test
    void testGetArticleDTOByIdNotFound() {
        when(articleRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<ArticleDTO> dtoOpt = articleService.getArticleDTOById(999L);

        assertFalse(dtoOpt.isPresent());
    }

    /**
     * Test retrieving all published articles only.
     */
    @Test
    void testGetAllPublishedArticles() {
        List<Article> published = List.of(article);
        when(articleRepository.findByPublishedTrue()).thenReturn(published);

        List<Article> result = articleService.getAllPublishedArticles();

        assertEquals(1, result.size());
        assertTrue(result.get(0).getPublished());
        assertEquals(article, result.get(0));
    }
}
