package com.movieflex.mapper;

import com.movieflex.dto.MovieDto;
import com.movieflex.entities.Movie;

import java.util.ArrayList;
import java.util.List;

public class MovieMapper {


    public List<MovieDto> movieToMovieDto(List<Movie> movies, String baseUrl) {
        List<MovieDto> movieDtos = new ArrayList<>();

        for(Movie movie : movies){
            MovieDto movieDto = new MovieDto(
                    movie.getMovieId(),
                    movie.getTitle(),
                    movie.getDirector(),
                    movie.getStudio(),
                    movie.getMovieCast(),
                    movie.getReleaseYear(),
                    movie.getPoster(),
                    this.createPosterUrl(baseUrl,movie.getPoster())
            );
            movieDtos.add(movieDto);
        }
        return movieDtos;
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
