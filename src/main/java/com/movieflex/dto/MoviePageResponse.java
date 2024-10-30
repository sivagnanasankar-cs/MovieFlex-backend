package com.movieflex.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class MoviePageResponse {
    private List<MovieDto> movieDtos;
    private Integer pageNumber;
    private Integer pageSize;
    private int totalElements;
    private int totalPage;
    private boolean isLast;
}
