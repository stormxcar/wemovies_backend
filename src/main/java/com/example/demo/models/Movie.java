package com.example.demo.models;

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
//@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
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


    @Override
    public String toString() {
        return "Movie{" +
                "movie_id=" + id +
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
