package com.movieflex.repositories;

import com.movieflex.entities.Movie;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieRepository extends JpaRepository<Movie,Integer > {
}
