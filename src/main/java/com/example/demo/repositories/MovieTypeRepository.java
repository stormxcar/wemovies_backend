/*
 * @ (#) MovieTypeRepository.java 1.0 12/24/2024
 *
 * Copyright (c) 2024 IUH.All rights reserved
 */
package com.example.demo.repositories;

import com.example.demo.models.MovieType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/*
 * @description
 * @author : Nguyen Truong An
 * @date : 12/24/2024
 * @version 1.0
 */
@Repository
public interface MovieTypeRepository extends JpaRepository<MovieType, Long> {
    @Query(value = "SELECT COUNT(m.movie_id) FROM movie AS m\n" +
            "JOIN movie_genre AS mg ON mg.movie_id = m.movie_id\n" +
            "JOIN movie_type AS mt ON mt.movie_type_id = mg.movie_type_id\n" +
            "WHERE mg.movie_type_id = :movieTypeId" , nativeQuery = true)
    int countMoviesByMovieTypeId(@Param("movieTypeId") Long movieTypeId);
}
