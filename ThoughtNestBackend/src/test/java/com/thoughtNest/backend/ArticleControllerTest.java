package com.thoughtNest.backend;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thoughtNest.backend.controller.ArticleController;
import com.thoughtNest.backend.dto.ArticleDTO;
import com.thoughtNest.backend.model.Article;
import com.thoughtNest.backend.model.User;
import com.thoughtNest.backend.service.ArticleService;
import com.thoughtNest.backend.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for ArticleController using Spring's MockMvc.
 *
 * This test class isolates the controller layer and verifies HTTP behavior using mocked dependencies.
 * It ensures correct behavior for article creation, retrieval, and deletion while handling authentication.
 */
class ArticleControllerTest {

    // Used to simulate HTTP requests/responses without starting a full web server
    private MockMvc mockMvc;

    // Injects mock dependencies into the controller
    @InjectMocks
    private ArticleController articleController;

    // Mocks the service that handles business logic for articles
    @Mock
    private ArticleService articleService;

    // Mocks the service used to retrieve authenticated user information
    @Mock
    private UserService userService;

    // Mocks the Spring Security principal object (used for authentication)
    @Mock
    private Authentication authentication;

    // Used to convert Java objects to JSON strings and vice versa
    private ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Set up MockMvc and initialize all mock objects before each test.
     */
    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this); // Initialize all @Mock and @InjectMocks
        mockMvc = MockMvcBuilders.standaloneSetup(articleController).build(); // Use standalone setup to test controller in isolation
    }

    /**
     * Test for successful article creation by an authenticated user.
     */
    @Test
    void createArticle_Success() throws Exception {
        // Given: a valid article and an authenticated user
        Article article = new Article();
        article.setTitle("Test Title");
        article.setContent("Test Content");

        User user = new User();
        user.setEmail("testuser");

        // When: the authenticated user's email is retrieved and the user exists
        when(authentication.getName()).thenReturn("testuser");
        when(userService.findByEmail("testuser")).thenReturn(Optional.of(user));

        // And: articleService saves the article and returns it
        article.setAuthor(user);
        Article savedArticle = new Article();
        savedArticle.setTitle("Test Title");
        savedArticle.setContent("Test Content");
        savedArticle.setAuthor(user);

        when(articleService.saveArticle(any(Article.class))).thenReturn(savedArticle);

        // Then: perform POST /api/articles and expect a 200 OK with correct JSON structure
        mockMvc.perform(post("/api/articles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(article))
                        .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Article created"))
                .andExpect(jsonPath("$.data.title").value("Test Title"));
    }

    /**
     * Test for article creation when the user is not found in the system.
     */
    @Test
    void createArticle_UserNotFound() throws Exception {
        Article article = new Article();

        // Simulate that the authenticated user is not found in DB
        when(authentication.getName()).thenReturn("unknownuser");
        when(userService.findByEmail("unknownuser")).thenReturn(Optional.empty());

        // Expect 401 Unauthorized with appropriate error message
        mockMvc.perform(post("/api/articles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(article))
                        .principal(authentication))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("User not found"));
    }

    /**
     * Test for retrieving all articles created by the authenticated user.
     */
    @Test
    void getMyArticles_Success() throws Exception {
        User user = new User();
        user.setEmail("testuser");

        // Simulate two articles authored by the user
        Article article1 = new Article();
        article1.setTitle("A1");
        article1.setAuthor(user);

        Article article2 = new Article();
        article2.setTitle("A2");
        article2.setAuthor(user);

        // Simulate valid user and articles returned from service
        when(authentication.getName()).thenReturn("testuser");
        when(userService.findByEmail("testuser")).thenReturn(Optional.of(user));
        when(articleService.getArticlesByUser(user)).thenReturn(List.of(article1, article2));

        // Expect 200 OK and a JSON array of size 2
        mockMvc.perform(get("/api/articles/my")
                        .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User articles retrieved"))
                .andExpect(jsonPath("$.data.length()").value(2));
    }

    /**
     * Test for retrieving user articles when user is not found.
     */
    @Test
    void getMyArticles_UserNotFound() throws Exception {
        when(authentication.getName()).thenReturn("unknownuser");
        when(userService.findByEmail("unknownuser")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/articles/my")
                        .principal(authentication))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("User not found"));
    }

    /**
     * Test for retrieving a single article by ID successfully.
     */
    @Test
    void getArticle_Success() throws Exception {
        Article article = new Article();
        article.setId(1L);
        article.setTitle("Title1");

        ArticleDTO articleDTO = new ArticleDTO(article);

        when(articleService.getArticleDTOById(1L)).thenReturn(Optional.of(articleDTO));

        mockMvc.perform(get("/api/articles/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Article retrieved"))
                .andExpect(jsonPath("$.data.title").value("Title1"));
    }

    /**
     * Test for retrieving a non-existent article by ID.
     */
    @Test
    void getArticle_NotFound() throws Exception {
        when(articleService.getArticleDTOById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/articles/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Article not found"));
    }

    /**
     * Test for successfully deleting an article authored by the authenticated user.
     */
    @Test
    void deleteArticle_Success() throws Exception {
        User user = new User();
        user.setEmail("testuser");

        Article article = new Article();
        article.setAuthor(user);

        when(authentication.getName()).thenReturn("testuser");
        when(userService.findByEmail("testuser")).thenReturn(Optional.of(user));
        when(articleService.getArticleById(1L)).thenReturn(Optional.of(article));
        doNothing().when(articleService).deleteArticle(1L);

        mockMvc.perform(delete("/api/articles/1")
                        .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Article deleted"));
    }

    /**
     * Test for deletion when the authenticated user is not the article owner.
     */
    @Test
    void deleteArticle_Unauthorized() throws Exception {
        User user1 = new User();
        user1.setEmail("user1");

        User user2 = new User();
        user2.setEmail("user2");

        Article article = new Article();
        article.setAuthor(user2); // Article is owned by another user

        when(authentication.getName()).thenReturn("user1");
        when(userService.findByEmail("user1")).thenReturn(Optional.of(user1));
        when(articleService.getArticleById(1L)).thenReturn(Optional.of(article));

        mockMvc.perform(delete("/api/articles/1")
                        .principal(authentication))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("Not authorized"));
    }

    /**
     * Test for deletion attempt on a non-existent article.
     */
    @Test
    void deleteArticle_NotFound() throws Exception {
        when(authentication.getName()).thenReturn("user1");
        when(userService.findByEmail("user1")).thenReturn(Optional.of(new User()));
        when(articleService.getArticleById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/articles/1")
                        .principal(authentication))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Article not found"));
    }
}
