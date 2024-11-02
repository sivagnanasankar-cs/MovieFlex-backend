package com.movieflex.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.movieflex.dto.MovieDto;
import com.movieflex.dto.Response;
import com.movieflex.service.MovieService;
import com.movieflex.utils.AppConstants;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/v1/movie")
public class MovieController {

    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/add-movie")
    public ResponseEntity<Response> addMovieHandler(@RequestPart MultipartFile file,
                                                    @RequestPart String  movieDto) throws IOException {
        MovieDto movieDto1 = convertToMovieDto(movieDto);
        Response response = movieService.addMovie(movieDto1, file);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{movieId}")
    public ResponseEntity<Response> getMovieHandler(@PathVariable Integer movieId) {
        Response response = movieService.getMovie(movieId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/all")
    public ResponseEntity<Response> getAllMovies() {
        Response response = movieService.getAllMovies();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/update/{movieId}")
    public ResponseEntity<Response> updateMovieHandler(@PathVariable Integer movieId,
                                                       @RequestPart MultipartFile file,
                                                       @RequestPart String movieDtoObj) throws IOException {
        if (file.isEmpty())
            file = null;
        MovieDto movieDto = convertToMovieDto(movieDtoObj);
        Response response = movieService.updateMovie(movieId, movieDto, file);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/delete/{movieId}")
    public ResponseEntity<Response> deleteMovieHandler(@PathVariable Integer movieId) throws IOException {
        Response response = movieService.deleteMovie(movieId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/allMoviesPage")
    public ResponseEntity<Response> getMoviesWithPagination(
            @RequestParam(defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
            @RequestParam(defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize)
    {
        Response response = movieService.getAllMoviesWithPagination(pageNumber, pageSize);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/allMoviesPageSort")
    public ResponseEntity<Response> getMoviesWithPaginationAndSorting(
            @RequestParam(defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
            @RequestParam(defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam(defaultValue = AppConstants.SORT_BY, required = false) String sortBy,
            @RequestParam(defaultValue = AppConstants.SORT_DIR, required = false) String dir)
    {
        Response response = movieService.getAllMoviesWithPaginationAndSorting(pageNumber,pageSize, sortBy, dir);
        return ResponseEntity.ok(response);
    }


    private MovieDto convertToMovieDto(String movieDtoObj) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(movieDtoObj, MovieDto.class);
    }

}
