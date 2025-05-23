package com.example.demo.services;

import com.example.demo.models.MovieType;

import java.util.List;

public interface MovieTypeSevice {
    List<MovieType> getAllMovieTypes();
    MovieType getMovieTypeById(Long id);
    MovieType saveMovieType(MovieType movieType);
    void deleteMovieTypeById(Long id);

    int countMoviesByMovieTypeId(Long movieTypeId);
    int countMovieTypes();
}
