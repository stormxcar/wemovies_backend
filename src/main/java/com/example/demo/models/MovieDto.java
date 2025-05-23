/*
 * @ (#) MovieDto.java 1.0 12/26/2024
 *
 * Copyright (c) 2024 IUH.All rights reserved
 */

package com.example.demo.models;

import lombok.Getter;
import lombok.Setter;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/*
 * @description
 * @author : Nguyen Truong An
 * @date : 12/26/2024
 * @version 1.0
 */
@Getter
@Setter
public class MovieDto {
    private Long id;
    private String title;
    private String description;
    private Year release_year;
    private String thumb_url;
    private String trailer;
    private String link;


    private Long countryId;
    private List<Long> movieTypeIds = new ArrayList<>();
    private List<Long> categoryIds = new ArrayList<>();

    public MovieDto() {
        // Default constructor for JSON deserialization
    }

    // Constructor to convert Movie to MovieDto
    public MovieDto(Movie movie) {
        if (movie != null) {
            this.id = movie.getMovie_id();
            this.title = movie.getTitle();
            this.description = movie.getDescription();
            this.release_year = movie.getRelease_year();
            this.thumb_url = movie.getThumb_url();
            this.trailer = movie.getTrailer();
            this.link = movie.getLink();
            this.countryId = movie.getCountry() != null ? movie.getCountry().getCountry_id() : null;
            this.movieTypeIds = movie.getMovieTypes() != null
                    ? movie.getMovieTypes().stream()
                    .map(MovieType::getMovie_type_id)
                    .collect(Collectors.toList())
                    : new ArrayList<>();
            this.categoryIds = movie.getMovieCategories() != null
                    ? movie.getMovieCategories().stream()
                    .map(Category::getCategory_id)
                    .collect(Collectors.toList())
                    : new ArrayList<>();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Year getRelease_year() {
        return release_year;
    }

    public void setRelease_year(Year release_year) {
        this.release_year = release_year;
    }

    public String getThumb_url() {
        return thumb_url;
    }

    public void setThumb_url(String thumb_url) {
        this.thumb_url = thumb_url;
    }

    public String getTrailer() {
        return trailer;
    }

    public void setTrailer(String trailer) {
        this.trailer = trailer;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public Long getCountryId() {
        return countryId;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
    }

    public List<Long> getMovieTypeIds() {
        return movieTypeIds;
    }

    public void setMovieTypeIds(List<Long> movieTypeIds) {
        this.movieTypeIds = movieTypeIds;
    }

    public List<Long> getCategoryIds() {
        return categoryIds;
    }

    public void setCategoryIds(List<Long> categoryIds) {
        this.categoryIds = categoryIds;
    }

    @Override
    public String toString() {
        return "MovieDto{" +
                "\nmovieId=" + id +
                ",\n title='" + title + '\'' +
                ",\n description='" + description + '\'' +
                ",\n releaseYear=" + release_year +
                ",\n thumbUrl='" + thumb_url + '\'' +
                ",\n link='" + link + '\'' +
                ",\n countryId=" + countryId +
                ",\n movieTypeIds=" + movieTypeIds +
                ",\n categoryIds=" + categoryIds +
                '}';
    }
    // No need for additional getters and setters due to Lombok
}