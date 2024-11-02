package com.movieflex.service;

import com.movieflex.dto.MovieDto;
import com.movieflex.dto.Response;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface MovieService {

    Response addMovie(MovieDto movieDto, MultipartFile file) throws IOException;

    Response getMovie(Integer movieId);

    Response getAllMovies();

    Response updateMovie(Integer movieId, MovieDto movieDto, MultipartFile file) throws IOException;

    Response deleteMovie(Integer movieId) throws IOException;

    Response getAllMoviesWithPagination(Integer pageNumber, Integer pageSize);

    Response getAllMoviesWithPaginationAndSorting(Integer pageNumber, Integer pageSize,
                                                           String sortBy, String dir, String query) throws IOException;
}
