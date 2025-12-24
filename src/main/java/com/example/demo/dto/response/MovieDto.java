package com.example.demo.dto.response;

import com.example.demo.models.Category;
import com.example.demo.models.Movie;
import com.example.demo.models.MovieType;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@Getter
@Setter
@Data
public class MovieDto {
    private UUID id;
    private String title;
    private String description;
    private Year release_year;
    private String thumb_url;
    private String trailer;
    private String link;


    private UUID countryId;
    private List<UUID> movieTypeIds = new ArrayList<>();
    private List<UUID> categoryIds = new ArrayList<>();

    private String titleByLanguage;
    private String status;
    private String totalEpisodes;
    private String director;
    private List<String> actors;
    private String duration;
    private String quality;
    private String vietSub;
    private String hot;
    private List<String> episodeLinks;

    public MovieDto() {
        // Default constructor for JSON deserialization
    }

    // Constructor to convert Movie to MovieDto
    public MovieDto(Movie movie) {
        if (movie != null) {
            this.id = movie.getId();
            this.title = movie.getTitle();
            this.description = movie.getDescription();
            this.release_year = movie.getRelease_year();
            this.thumb_url = movie.getThumb_url();
            this.trailer = movie.getTrailer();
            this.link = movie.getLink();
            this.countryId = movie.getCountry() != null ? movie.getCountry().getId() : null;
            this.movieTypeIds = movie.getMovieTypes() != null
                    ? movie.getMovieTypes().stream()
                    .map(MovieType::getId)
                    .collect(Collectors.toList())
                    : new ArrayList<>();
            this.categoryIds = movie.getMovieCategories() != null
                    ? movie.getMovieCategories().stream()
                    .map(Category::getId)
                    .collect(Collectors.toList())
                    : new ArrayList<>();
        }
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