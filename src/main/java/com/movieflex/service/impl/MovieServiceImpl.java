package com.movieflex.service.impl;

import com.movieflex.constants.MessageCodes;
import com.movieflex.dto.MovieDto;
import com.movieflex.dto.MoviePageResponse;
import com.movieflex.dto.Response;
import com.movieflex.entities.Movie;
import com.movieflex.exceptions.FileExistsException;
import com.movieflex.exceptions.MovieNotFoundException;
import com.movieflex.mapper.MovieMapper;
import com.movieflex.repositories.MovieRepository;
import com.movieflex.service.FileService;
import com.movieflex.service.MovieService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;


@Service
public class MovieServiceImpl implements MovieService {

    private final MovieRepository movieRepository;
    private final FileService fileService;
    private final MovieMapper movieMapper;

    @Value("${project.poster}")
    private String path;

    @Value("${base.url}")
    private String baseUrl;

    public MovieServiceImpl(MovieRepository movieRepository, FileService fileService) {
        this.movieRepository = movieRepository;
        this.fileService = fileService;
        this.movieMapper = new MovieMapper();
    }

    @Override
    public Response addMovie(MovieDto movieDto, MultipartFile file) throws IOException {
        if(file.isEmpty()){
            return Response.builder()
                    .statusCode(MessageCodes.INTERNAL_SERVER_ERROR)
                    .statusDescription("File is empty! Please send another file")
                    .build();
        }
        String filePath = String.valueOf(Paths.get(path  + File.separator + file.getOriginalFilename()));
        if(Files.exists(Paths.get(filePath))){
            throw new FileExistsException("File already exists! Please enter another file name!");
        }
        // 1. upload the file
        String uploadedFileName = fileService.uploadFile(path, file);

        // 2. set the value of field poster as fileName
        movieDto.setPoster(uploadedFileName);

        // 3. map dto to movie obj
        Movie movie = movieMapper.movieDtoToMovie(movieDto);

        // 4. to save the movie obj -> same movie obj
        Movie savedMovie = movieRepository.save(movie);

        String posterUrl = baseUrl + "/file/" + uploadedFileName;
        // 6. map movie object to dto obj and return
        MovieDto movieDto1 = movieMapper.movieToMovieDto(movie, baseUrl);
        return Response.builder()
                .statusCode("201")
                .statusDescription("Movie Added Successfully")
                .data(movieDto1)
                .build();

    }

    @Override
    public Response getMovie(Integer movieId) {

        // 1. check the data in db if exists fetch the data of given id
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new MovieNotFoundException("movie not found with the id = " + movieId));
        // 2. generate poster url
        String posterUrl = baseUrl + "/file/" + movie.getPoster();

        // 3. map to moviedto object and return
        MovieDto movieDto =  movieMapper.movieToMovieDto(movie, baseUrl);
        return Response.builder()
                .statusCode(MessageCodes.OK)
                .statusDescription("OK")
                .data(movieDto)
                .build();
    }

    @Override
    public Response getAllMovies() {
        System.err.println(baseUrl);
        List<Movie> movies = movieRepository.findAll();
        List<MovieDto> movieDtoList= movieMapper.movieToMovieDto(movies, baseUrl);
        return Response.builder()
                .statusCode(MessageCodes.OK)
                .statusDescription("OK")
                .data(movieDtoList)
                .build();
    }

    @Override
    public Response updateMovie(Integer movieId, MovieDto movieDto, MultipartFile file) throws IOException {
        // 1. Check if movie exists with the given movieId
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new MovieNotFoundException("movie not found with the id = "+ movieId));

        // 2. if file is null do nothing
        // if file is not null, then delte the existing file associated with the record
        // and upload the new file
        String fileName = movie.getPoster();
        if(file != null){
            Files.deleteIfExists(Paths.get(path + File.separator + fileName));
            fileName = fileService.uploadFile(path , file);
        }

        // 3. set moviedto's poster value , according to step 2
        movieDto.setPoster(fileName);

        // 4. map it to movie obj
        movieDto.setMovieId(movieId);
        Movie movieToUpdate = movieMapper.movieDtoToMovie(movieDto);
        Movie updatedMovie = movieRepository.save(movieToUpdate);

        // 5. generate poster url for it
        String posterUrl = baseUrl + "/file/" + movie.getPoster();
        MovieDto movieDto1 = movieMapper.movieToMovieDto(updatedMovie, baseUrl);
        return Response.builder().statusCode(MessageCodes.OK).statusDescription("Details updated Successfully").data(movieDto1).build();
    }

    @Override
    public Response deleteMovie(Integer movieId) throws IOException {
        // 1. check if the movie object exists in db
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new MovieNotFoundException("movie not found with the id = "+ movieId));

        // 2. delete the file associated with this object
        Files.deleteIfExists(Paths.get(path + File.separator + movie.getPoster()));

        // 3. delete the movie object
        movieRepository.delete(movie);
        return Response.builder()
                .statusCode(MessageCodes.OK)
                .statusDescription("Movie Deleted successfully")
                .data("Movie deleted with id = "+ movieId)
                .build();
    }

    @Override
    public Response getAllMoviesWithPagination(Integer pageNumber, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<Movie> moviePages = movieRepository.findAll(pageable);
        List<Movie> movies = moviePages.getContent();
        List<MovieDto> movieDtos = movieMapper.movieToMovieDto(movies, baseUrl);

        MoviePageResponse moviePageResponse = new MoviePageResponse(movieDtos, pageNumber, pageSize,
                                    (int) moviePages.getTotalElements(),
                                    moviePages.getTotalPages(),
                                    moviePages.isLast());

        return Response.builder()
                .statusCode(MessageCodes.OK)
                .statusDescription("Movies Found!")
                .data(moviePageResponse)
                .build();
    }

    @Override
    public Response getAllMoviesWithPaginationAndSorting(Integer pageNumber, Integer pageSize, String sortBy, String dir) {
        Sort sort = dir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        Page<Movie> moviePages = movieRepository.findAll(pageable);
        List<Movie> movies = moviePages.getContent();
        List<MovieDto> movieDtos = movieMapper.movieToMovieDto(movies, baseUrl);

        MoviePageResponse moviePageResponse = new MoviePageResponse(movieDtos, pageNumber, pageSize,
                (int) moviePages.getTotalElements(),
                moviePages.getTotalPages(),
                moviePages.isLast()
        );

        return Response.builder()
                .statusCode(MessageCodes.OK)
                .statusDescription("Movies Found!")
                .data(moviePageResponse)
                .build();
    }
}
