/*
 * @ (#) MovieTypeSevice.java 1.0 12/23/2024
 *
 * Copyright (c) 2024 IUH.All rights reserved
 */
package com.example.demo.services;

import com.example.demo.models.MovieType;

import java.util.List;

/*
 * @description
 * @author : Nguyen Truong An
 * @date : 12/23/2024
 * @version 1.0
 */
public interface MovieTypeSevice {
    List<MovieType> getAllMovieTypes();
    MovieType getMovieTypeById(Long id);
    MovieType saveMovieType(MovieType movieType);
    void deleteMovieTypeById(Long id);

    int countMoviesByMovieTypeId(Long movieTypeId);
    int countMovieTypes();
}
