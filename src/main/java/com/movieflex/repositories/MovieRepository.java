package com.movieflex.repositories;

import com.movieflex.entities.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MovieRepository extends JpaRepository<Movie,Integer > {

    @Query("SELECT m FROM Movie m WHERE " +
            "LOWER(m.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(m.director) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "EXISTS (SELECT c FROM m.movieCast c WHERE LOWER(c) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<Movie> searchByTitleDirectorOrCast(@Param("query") String query, Pageable pageable);

}
