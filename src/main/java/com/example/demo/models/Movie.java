package com.example.demo.models;

import com.example.demo.utils.YoutubeUrlUtil;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.Year;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(name = "movie", indexes = {
    @Index(name = "idx_movie_slug", columnList = "slug", unique = true)
})
public class Movie extends BaseEntity {
    private String title;

    @Column(unique = true)
    private String slug;

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

    @Column(name = "total_episodes", nullable = true)
    private Integer totalEpisodes; // cho phep null với Integer

    @Column(name = "episode_links", columnDefinition = "TEXT")
    @Deprecated
    private String episodeLinks;

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Episode> episodes = new ArrayList<>();

    @Column(columnDefinition = "LONGTEXT")
    private String description;
    private Year release_year;
    private String link;
    private String thumb_url;

    // Banner image for large background display
    private String banner_url;

    // Age rating for content classification
    @Enumerated(EnumType.STRING)
    @Column(name = "age_rating")
    private AgeRating ageRating;

    @ManyToOne
    @JoinColumn(name = "country_id")
    private Country country;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "movie_genre",
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "movie_type_id")
    )
    @Builder.Default
    private Set<MovieType> movieTypes = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "movie_category",
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    @Builder.Default
    private Set<Category> movieCategories = new HashSet<>();

    @Column(name = "actors", columnDefinition = "TEXT")
    private String actors; // Store as comma-separated string

    public enum Quality {
        SD, HD, FULL_HD, _4K
    }

    public enum AgeRating {
        P,      // Phù hợp mọi lứa tuổi
        T7,     // Từ 7 tuổi trở lên
        T13,    // Từ 13 tuổi trở lên
        T16,    // Từ 16 tuổi trở lên
        T18,    // Từ 18 tuổi trở lên
    }

    // Helper methods for actors
    public Set<String> getActorsSet() {
        if (actors == null || actors.trim().isEmpty()) {
            return new HashSet<>();
        }
        return new HashSet<>(Arrays.asList(actors.split(",")));
    }

    public void setActorsSet(Set<String> actorsSet) {
        if (actorsSet == null || actorsSet.isEmpty()) {
            this.actors = null;
        } else {
            this.actors = String.join(",", actorsSet);
        }
    }
}
