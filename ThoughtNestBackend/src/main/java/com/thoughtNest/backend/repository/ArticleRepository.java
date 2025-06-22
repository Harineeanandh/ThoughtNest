package com.thoughtNest.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.thoughtNest.backend.model.Article;
import com.thoughtNest.backend.model.User;

/**
 * Repository interface for performing CRUD operations on {@link Article} entities.
 * Extends Spring Data JPA's {@link JpaRepository} to inherit standard methods.
 */
public interface ArticleRepository extends JpaRepository<Article, Long> {

    /**
     * Finds all articles authored by a specific user.
     *
     * @param author the {@link User} whose articles are to be fetched
     * @return list of articles written by the given user
     */
    @Query("SELECT a FROM Article a JOIN FETCH a.author WHERE a.author = :author")
    List<Article> findByAuthor(User author);

    /**
     * Retrieves all articles that are published.
     *
     * @return list of articles with published status set to true
     */
    @Query("SELECT a FROM Article a JOIN FETCH a.author WHERE a.published = true")
    List<Article> findByPublishedTrue();

}
