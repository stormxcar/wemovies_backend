package com.example.demo.models;

import com.example.demo.utils.YoutubeUrlUtil;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.Year;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
@Data
//@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "movie")
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
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

    @ElementCollection
    @CollectionTable(name = "movie_actors", joinColumns = @JoinColumn(name = "movie_id"))
    @Column(name = "actor")
    @Builder.Default
    private Set<String> actors = new HashSet<>();

    public enum Quality {
        SD, HD, FULL_HD, _4K
    }
}
