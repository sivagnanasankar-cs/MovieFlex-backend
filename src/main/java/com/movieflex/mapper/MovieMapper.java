package com.movieflex.mapper;

import com.movieflex.dto.MovieDto;
import com.movieflex.entities.Movie;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MovieMapper {


    public List<MovieDto> movieToMovieDto(List<Movie> movies, String baseUrl) {
        return movies.stream()
                .map(movie -> movieToMovieDto(movie, baseUrl))
                .collect(Collectors.toList());
    }

    public MovieDto movieToMovieDto(Movie movie, String baseUrl) {
        return  new MovieDto(
                movie.getMovieId(),
                movie.getTitle(),
                movie.getDirector(),
                movie.getStudio(),
                movie.getMovieCast(),
                movie.getReleaseYear(),
                movie.getPoster(),
                this.createPosterUrl(baseUrl, movie.getPoster())
        );
    }

    public Movie movieDtoToMovie(MovieDto movieDto) {
        return new Movie(
                movieDto.getMovieId(),
                movieDto.getTitle(),
                movieDto.getDirector(),
                movieDto.getStudio(),
                movieDto.getMovieCast(),
                movieDto.getReleaseYear(),
                movieDto.getPoster()
        );
    }

    public String createPosterUrl(String baseUrl, String poster){
        return  baseUrl + "/file/get/" + poster;
    }
}
