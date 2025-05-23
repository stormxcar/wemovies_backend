/*
 * @ (#) Movie.java 1.0 12/23/2024
 *
 * Copyright (c) 2024 IUH.All rights reserved
 */

package com.example.demo.models;

/*
 * @description
 * @author : Nguyen Truong An
 * @date : 12/23/2024
 * @version 1.0
 */
import com.example.demo.utils.YoutubeUrlUtil;
import jakarta.persistence.*;
import lombok.*;

import java.time.Year;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long movie_id;
    private String title;

    // bonus
    private String titleByLanguage;
    private String status;
    private String director;
    private Integer duration;
    private String trailer;
    private Quality quality;
    private boolean vietSub;

    private long views;
    private boolean hot;

    @Column(name = "total_episodes" , nullable = true)
    private Integer totalEpisodes; // cho phep null với Integer
    @Column(name = "episode_links", columnDefinition = "LONGTEXT")
    private String episodeLinks;

    @Column(columnDefinition = "LONGTEXT")
    private String description;
    private Year release_year;
    private String link;
    private String thumb_url;

    @ManyToOne
    @JoinColumn(name = "country_id")
    private Country country;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "movie_genre",
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "movie_type_id")
    )
    private Set<MovieType> movieTypes = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "movie_category",
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private Set<Category> movieCategories = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "movie_actors", joinColumns = @JoinColumn(name = "movie_id"))
    @Column(name = "actor")
    private Set<String> actors = new HashSet<>();

    public enum Quality {
        SD, HD, FULL_HD, _4K
    }

    // get the video id from the trailer link
    public String getEmbedLink() {
        String videoId = YoutubeUrlUtil.extractVideoId(this.trailer);
        if (videoId != null && !videoId.isEmpty()) {
            return "https://www.youtube.com/embed/" + videoId;
        }
        return "";
    }

//    System.out.println("Embed link: " + movie.getEmbedLink());


    // Method to calculate average duration
//    public int getAverageDuration() {
//        if (episodeDurations == null || episodeDurations.isEmpty()) {
//            return 0;
//        }
//        String[] durations = episodeDurations.split(",");
//        int totalDuration = 0;
//        for (String duration : durations) {
//            totalDuration += Integer.parseInt(duration.trim());
//        }
//        return totalDuration / durations.length;
//    }


    public Long getMovie_id() {
        return movie_id;
    }

    public void setMovie_id(Long movie_id) {
        this.movie_id = movie_id;
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

    public String getLink() {
        return link;
    }

    public Integer getTotalEpisodes() {
        return totalEpisodes;
    }

    public void setTotalEpisodes(Integer totalEpisodes) {
        this.totalEpisodes = totalEpisodes;
    }

    public boolean isHot() {
        return hot;
    }

    public void setHot(boolean hot) {
        this.hot = hot;
    }

    public String getEpisodeLinks() {
        return episodeLinks;
    }

    public void setEpisodeLinks(String episodeLinks) {
        this.episodeLinks = episodeLinks;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getThumb_url() {
        return thumb_url;
    }

    public void setThumb_url(String thumb_url) {
        this.thumb_url = thumb_url;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public Set<MovieType> getMovieTypes() {
        return movieTypes;
    }

    public void setMovieTypes(Set<MovieType> movieTypes) {
        this.movieTypes = movieTypes;
    }

    public Set<Category> getMovieCategories() {
        return movieCategories;
    }

    public void setMovieCategories(Set<Category> movieCategories) {
        this.movieCategories = movieCategories;
    }

    public String getTitleByLanguage() {
        return titleByLanguage;
    }

    public void setTitleByLanguage(String titleByLanguage) {
        this.titleByLanguage = titleByLanguage;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public String getTrailer() {
        return trailer;
    }

    public void setTrailer(String trailer) {
        this.trailer = trailer;
    }

    public Quality getQuality() {
        return quality;
    }

    public void setQuality(Quality quality) {
        this.quality = quality;
    }

    public boolean isVietSub() {
        return vietSub;
    }

    public void setVietSub(boolean vietSub) {
        this.vietSub = vietSub;
    }

    public long getViews() {
        return views;
    }

    public void setViews(long views) {
        this.views = views;
    }

    public Set<String> getActors() {
        return actors;
    }

    public void setActors(Set<String> actors) {
        this.actors = actors;
    }

    @Override
    public String toString() {
        return "Movie{" +
                "movie_id=" + movie_id +
                ",\n title='" + title + '\'' +
                ",\n titleByLanguage='" + titleByLanguage + '\'' +
                ",\n status='" + status + '\'' +
                ",\n director='" + director + '\'' +
                ",\n duration=" + duration +
                ",\n trailer='" + trailer + '\'' +
                ",\n quality=" + quality +
                ",\n vietSub=" + vietSub +
                ",\n views=" + views +
                ",\n hot=" + hot +
                ",\n totalEpisodes=" + totalEpisodes +
                ",\n episodeLinks='" + episodeLinks + '\'' +
                ",\n description='" + description + '\'' +
                ",\n release_year=" + release_year +
                ",\n link='" + link + '\'' +
                ",\n thumb_url='" + thumb_url + '\'' +
                ",\n country=" + country +
                ",\n movieTypes=" + movieTypes +
                ",\n movieCategories=" + movieCategories +
                ",\n actors=" + actors +
                '}';
    }
}
