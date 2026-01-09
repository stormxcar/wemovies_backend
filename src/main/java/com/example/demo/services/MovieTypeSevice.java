package com.example.demo.services;

import com.example.demo.models.MovieType;

import java.util.List;
import java.util.UUID;

public interface MovieTypeSevice {
    List<MovieType> getAllMovieTypes();
    MovieType getMovieTypeById(UUID id);
    MovieType getMovieTypeBySlug(String slug);
    MovieType saveMovieType(MovieType movieType);
    void deleteMovieTypeById(UUID id);

    int countMoviesByMovieTypeId(UUID movieTypeId);
    int countMovieTypes();
}
