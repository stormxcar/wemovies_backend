package com.example.demo.controllers;

import com.example.demo.dto.response.ApiResponse;
import com.example.demo.models.*;
import com.example.demo.services.Impls.CloudinaryService;
import com.example.demo.services.CategoryService;
import com.example.demo.services.CountryService;
import com.example.demo.services.MovieService;
import com.example.demo.services.MovieTypeSevice;
import com.example.demo.services.NotificationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.Year;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

@RestController
@RequestMapping("/api/movies")
public class MovieController {
    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
            "createdAt", "updatedAt", "title", "release_year", "views", "duration", "hot"
    );

    @Autowired
    private MovieService movieService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private MovieTypeSevice movieTypeSevice;

    @Autowired
    private CountryService countryService;

    @Autowired
    private CloudinaryService cloudinaryService;

    @Autowired
    private NotificationService notificationService;

    @GetMapping()
    public ResponseEntity<ApiResponse<Map<String, Object>>> movies(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        try {
            Pageable pageable = buildPageable(page, size, sortBy, sortDir);
            Page<Movie> moviePage = movieService.getAllMovies(pageable);
            if (moviePage.isEmpty()) {
                return new ResponseEntity<>(new ApiResponse<>(true, "No movies found", null), HttpStatus.NO_CONTENT);
            }
            return ResponseEntity.ok(new ApiResponse<>(true, "Movies retrieved successfully", toPagedPayload(moviePage)));
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(new ApiResponse<>(false, e.getMessage(), null), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "An error occurred while retrieving movies", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Movie>> getMovieById(@PathVariable UUID id) {
        try {
            Movie movie = movieService.getMovieById(id);
            if (movie == null) {
                return new ResponseEntity<>(new ApiResponse<>(false, "Movie not found", null), HttpStatus.NOT_FOUND);
            }
            return ResponseEntity.ok(new ApiResponse<>(true, "Movie retrieved successfully", movie));
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "An error occurred while retrieving the movie", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/slug/{slug}")
    public ResponseEntity<ApiResponse<Movie>> getMovieBySlug(@PathVariable String slug) {
        try {
            Movie movie = movieService.getMovieBySlug(slug);
            if (movie == null) {
                return new ResponseEntity<>(new ApiResponse<>(false, "Movie not found", null), HttpStatus.NOT_FOUND);
            }
            return ResponseEntity.ok(new ApiResponse<>(true, "Movie retrieved successfully", movie));
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "An error occurred while retrieving the movie", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/addPage")
    public ResponseEntity<ApiResponse<Object>> addPage() {
        try {
            Page<Movie> moviePage = movieService.getAllMovies(PageRequest.of(0, 20));
            Map<String, Object> response = new HashMap<>();
            response.put("movies", moviePage.getContent());
            response.put("countMovie", movieService.countMovies());
            response.put("categories", categoryService.getAllCategory());
            response.put("types", movieTypeSevice.getAllMovieTypes());
            response.put("countries", countryService.getAllCountries());
            return ResponseEntity.ok(new ApiResponse<>(true, "Page data retrieved successfully", response));
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "An error occurred while retrieving page data", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/add")
    public ResponseEntity<ApiResponse<Movie>> addMovie(@RequestParam("title") String title,
                                                       @RequestParam("description") String description,
                                                       @RequestParam("director") String director,
                                                       @RequestParam("duration") Integer duration,
                                                       @RequestParam("hot") boolean hot,
                                                       @RequestParam("link") String link,
                                                       @RequestParam("quality") String quality,
                                                       @RequestParam("release_year") Integer releaseYear,
                                                       @RequestParam("status") String status,
                                                       @RequestParam(value = "thumb_url", required = false) String thumbUrl,
                                                       @RequestParam(value = "thumbnailFile", required = false) MultipartFile thumbnailFile,
                                                       @RequestParam(value = "banner_url", required = false) String bannerUrl,
                                                       @RequestParam(value = "bannerFile", required = false) MultipartFile bannerFile,
                                                       @RequestParam("titleByLanguage") String titleByLanguage,
                                                       @RequestParam(value = "totalEpisodes", required = false) Integer totalEpisodes,
                                                       @RequestParam("trailer") String trailer,
                                                       @RequestParam("vietSub") boolean vietSub,
                                                       @RequestParam("actors") String[] actors,
                                                       @RequestParam(value = "episodeLinks", required = false) List<String> episodeLinks,
                                                       @RequestParam("countryId") UUID countryId,
                                                       @RequestParam("movieTypeIds") List<UUID> movieTypeIds,
                                                       @RequestParam("categoryIds") List<UUID> categoryIds,
                                                       @RequestParam(value = "ageRating", required = false) String ageRating) {
        try {
            // Create Movie object from parameters
            Movie movie = new Movie();
            movie.setTitle(title);
            // Upload thumbnail + banner concurrently to reduce API latency
            String finalThumbUrl;
            String finalBannerUrl;
            try {
                CompletableFuture<String> thumbFuture = resolveMediaAsync(thumbnailFile, thumbUrl, null, "thumbnail");
                CompletableFuture<String> bannerFuture = resolveMediaAsync(bannerFile, bannerUrl, null, "banner");
                finalThumbUrl = thumbFuture.join();
                finalBannerUrl = bannerFuture.join();
            } catch (CompletionException e) {
                Throwable cause = e.getCause() != null ? e.getCause() : e;
                return new ResponseEntity<>(new ApiResponse<>(false, cause.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
            }

            movie.setDescription(description);
            movie.setDirector(director);
            movie.setDuration(duration);
            movie.setHot(hot);
            movie.setLink(link);
            movie.setQuality(Movie.Quality.valueOf(quality.toUpperCase().replace(" ", "_")));
            movie.setRelease_year(Year.of(releaseYear));
            movie.setStatus(status);
            movie.setThumb_url(finalThumbUrl);
            movie.setBanner_url(finalBannerUrl);
            movie.setTitleByLanguage(titleByLanguage);
            movie.setTotalEpisodes(totalEpisodes);
            movie.setTrailer(trailer);
            movie.setVietSub(vietSub);

            // Set age rating if provided
            if (ageRating != null && !ageRating.trim().isEmpty()) {
                try {
                    movie.setAgeRating(Movie.AgeRating.valueOf(ageRating.toUpperCase()));
                } catch (IllegalArgumentException e) {
                    return new ResponseEntity<>(new ApiResponse<>(false, "Invalid age rating: " + ageRating + ". Valid values: P, T7, T13, T16, T18, T21", null), HttpStatus.BAD_REQUEST);
                }
            }

            Set<String> actorSet = new HashSet<>();
            for (String actor : actors) {
                if (!actor.trim().isEmpty()) {
                    actorSet.add(actor);
                }
            }

            Country country = countryService.getCountryById(countryId);
            movie.setCountry(country);

            Set<MovieType> movieTypes = new HashSet<>();
            for (UUID id : movieTypeIds) {
                MovieType movieType = movieTypeSevice.getMovieTypeById(id);
                movieTypes.add(movieType);
            }
            movie.setMovieTypes(movieTypes);

            Set<Category> categories = new HashSet<>();
            for (UUID id : categoryIds) {
                Category category = categoryService.getCategoryById(id);
                categories.add(category);
            }
            movie.setMovieCategories(categories);

            movie.setActorsSet(actorSet);

            // Handle episodes if provided
            if (episodeLinks != null && !episodeLinks.isEmpty()) {
                movie.setTotalEpisodes(episodeLinks.size());
                movie.setLink(null); // Clear link for phim bộ
                List<Episode> episodes = new ArrayList<>();
                for (int i = 0; i < episodeLinks.size(); i++) {
                    Episode episode = new Episode();
                    episode.setEpisodeNumber(i + 1); // Assign episode number (Tập 1, Tập 2, ...)
                    episode.setLink(episodeLinks.get(i));
                    episode.setMovie(movie);
                    episodes.add(episode);
                }
                movie.setEpisodes(episodes);
            } else {
                movie.setTotalEpisodes(null);
                movie.setEpisodes(new ArrayList<>());
            }

            Movie savedMovie = movieService.saveMovie(movie);
            
            // Gửi thông báo realtime cho tất cả users về phim mới
            try {
                String movieUrl = "/movies/" + savedMovie.getId();
                notificationService.sendBroadcastToAllUsers(
                    Notification.NotificationType.NEW_MOVIE,
                    "🎬 Phim mới: " + savedMovie.getTitle(),
                    "Phim '" + savedMovie.getTitle() + "' vừa được thêm vào thư viện. Xem ngay!",
                    movieUrl,
                    savedMovie,
                    Map.of(
                        "movieId", savedMovie.getId().toString(),
                        "movieTitle", savedMovie.getTitle(),
                        "releaseYear", savedMovie.getRelease_year() != null ? savedMovie.getRelease_year().toString() : null,
                        "quality", savedMovie.getQuality(),
                        "addedAt", System.currentTimeMillis()
                    )
                );
            } catch (Exception e) {
                // Log error but don't fail the movie creation
                System.err.println("Failed to send new movie notification: " + e.getMessage());
            }
            
            return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(true, "Movie added successfully", savedMovie));
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(new ApiResponse<>(false, "An error occurred while adding the movie: " + e.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

//    @GetMapping("/admin/api/{id}")
//    public ResponseEntity<ApiResponse<MovieDto>> editMovie(@PathVariable Long id) {
//        try {
//            Movie movie = movieService.getMovieById(id);
//            MovieDto movieDto = new MovieDto(movie);
//            return ResponseEntity.ok(new ApiResponse<>(true, "Movie retrieved successfully", movieDto));
//        } catch (Exception e) {
//            return new ResponseEntity<>(new ApiResponse<>(false, "An error occurred while retrieving the movie", null), HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }

//    @PutMapping("/update/{id}")
//    public ResponseEntity<ApiResponse<Movie>> updateMovie(@PathVariable Long id,
//                                                          @RequestBody Movie movie,
//                                                          @RequestParam("countryId") Long countryId,
//                                                          @RequestParam(value = "movieTypeIds", required = false) List<Long> movieTypeIds,
//                                                          @RequestParam(value = "categoryIds", required = false) List<Long> categoryIds) {
//        try {
//            Country country = countryService.getCountryById(countryId);
//            movie.setCountry(country);
//
//            if (movieTypeIds != null) {
//                Set<MovieType> movieTypes = new HashSet<>();
//                for (Long typeId : movieTypeIds) {
//                    MovieType movieType = movieTypeSevice.getMovieTypeById(typeId);
//                    movieTypes.add(movieType);
//                }
//                movie.setMovieTypes(movieTypes);
//            }
//
//            if (categoryIds != null) {
//                Set<Category> categories = new HashSet<>();
//                for (Long categoryId : categoryIds) {
//                    Category category = categoryService.getCategoryById(categoryId);
//                    categories.add(category);
//                }
//                movie.setMovieCategories(categories);
//            }
//
//            Movie updatedMovie = movieService.updateMovie(id, movie);
//            return ResponseEntity.ok(new ApiResponse<>(true, "Movie updated successfully", updatedMovie));
//        } catch (Exception e) {
//            return new ResponseEntity<>(new ApiResponse<>(false, "An error occurred while updating the movie", null), HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse<Movie>> updateMovie(@PathVariable UUID id,
                                                          @RequestParam("title") String title,
                                                          @RequestParam("description") String description,
                                                          @RequestParam("director") String director,
                                                          @RequestParam("duration") Integer duration,
                                                          @RequestParam("hot") boolean hot,
                                                          @RequestParam("link") String link,
                                                          @RequestParam("quality") String quality,
                                                          @RequestParam("release_year") Integer releaseYear,
                                                          @RequestParam("status") String status,
                                                          @RequestParam(value = "thumb_url", required = false) String thumbUrl,
                                                          @RequestParam(value = "thumbnailFile", required = false) MultipartFile thumbnailFile,
                                                          @RequestParam(value = "banner_url", required = false) String bannerUrl,
                                                          @RequestParam(value = "bannerFile", required = false) MultipartFile bannerFile,
                                                          @RequestParam("titleByLanguage") String titleByLanguage,
                                                          @RequestParam(value = "totalEpisodes", required = false) Integer totalEpisodes,
                                                          @RequestParam("trailer") String trailer,
                                                          @RequestParam("vietSub") boolean vietSub,
                                                          @RequestParam("actors") String[] actors,
                                                          @RequestParam(value = "episodeLinks", required = false) List<String> episodeLinks,
                                                          @RequestParam("countryId") UUID countryId,
                                                          @RequestParam("movieTypeIds") List<UUID> movieTypeIds,
                                                          @RequestParam("categoryIds") List<UUID> categoryIds,
                                                          @RequestParam(value = "ageRating", required = false) String ageRating) {

        try {
            Movie existingMovie = movieService.getMovieById(id);
            if (existingMovie == null) {
                return new ResponseEntity<>(new ApiResponse<>(false, "Movie not found", null), HttpStatus.NOT_FOUND);
            }

            // Handle thumbnail upload
            String finalThumbUrl;
            String finalBannerUrl;
            try {
                CompletableFuture<String> thumbFuture = resolveMediaAsync(
                        thumbnailFile,
                        thumbUrl,
                        existingMovie.getThumb_url(),
                        "thumbnail"
                );
                CompletableFuture<String> bannerFuture = resolveMediaAsync(
                        bannerFile,
                        bannerUrl,
                        existingMovie.getBanner_url(),
                        "banner"
                );
                finalThumbUrl = thumbFuture.join();
                finalBannerUrl = bannerFuture.join();
            } catch (CompletionException e) {
                Throwable cause = e.getCause() != null ? e.getCause() : e;
                return new ResponseEntity<>(new ApiResponse<>(false, cause.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
            }

            // Update fields
            existingMovie.setTitle(title);
            existingMovie.setDescription(description);
            existingMovie.setDirector(director);
            existingMovie.setDuration(duration);
            existingMovie.setHot(hot);
            existingMovie.setLink(link);
            existingMovie.setQuality(Movie.Quality.valueOf(quality.toUpperCase().replace(" ", "_")));
            existingMovie.setRelease_year(Year.of(releaseYear));
            existingMovie.setStatus(status);
            existingMovie.setThumb_url(finalThumbUrl);
            existingMovie.setBanner_url(finalBannerUrl);
            existingMovie.setTitleByLanguage(titleByLanguage);
            existingMovie.setTotalEpisodes(totalEpisodes);
            existingMovie.setTrailer(trailer);
            existingMovie.setVietSub(vietSub);

            // Update age rating if provided
            if (ageRating != null && !ageRating.trim().isEmpty()) {
                try {
                    existingMovie.setAgeRating(Movie.AgeRating.valueOf(ageRating.toUpperCase()));
                } catch (IllegalArgumentException e) {
                    return new ResponseEntity<>(new ApiResponse<>(false, "Invalid age rating: " + ageRating + ". Valid values: P, T7, T13, T16, T18, T21", null), HttpStatus.BAD_REQUEST);
                }
            } else {
                // Keep existing age rating if not provided
                existingMovie.setAgeRating(existingMovie.getAgeRating());
            }

            Set<String> actorSet = new HashSet<>();
            for (String actor : actors) {
                if (!actor.trim().isEmpty()) {
                    actorSet.add(actor);
                }
            }
            existingMovie.setActorsSet(actorSet);

            Country country = countryService.getCountryById(countryId);
            existingMovie.setCountry(country);

            Set<MovieType> movieTypes = new HashSet<>();
            for (UUID mid : movieTypeIds) {
                MovieType movieType = movieTypeSevice.getMovieTypeById(mid);
                movieTypes.add(movieType);
            }
            existingMovie.setMovieTypes(movieTypes);

            Set<Category> categories = new HashSet<>();
            for (UUID cid : categoryIds) {
                Category category = categoryService.getCategoryById(cid);
                categories.add(category);
            }
            existingMovie.setMovieCategories(categories);

            // Handle episodes if provided
            if (episodeLinks != null && !episodeLinks.isEmpty()) {
                existingMovie.setTotalEpisodes(episodeLinks.size());
                existingMovie.setLink(null); // Clear link for phim bộ
                existingMovie.getEpisodes().clear();
                List<Episode> episodes = new ArrayList<>();
                for (int i = 0; i < episodeLinks.size(); i++) {
                    Episode episode = new Episode();
                    episode.setEpisodeNumber(i + 1); // Assign episode number (Tập 1, Tập 2, ...)
                    episode.setLink(episodeLinks.get(i));
                    episode.setMovie(existingMovie);
                    episodes.add(episode);
                }
                existingMovie.setEpisodes(episodes);
            } else {
                existingMovie.setTotalEpisodes(totalEpisodes);
                existingMovie.setLink(link);
                existingMovie.getEpisodes().clear();
            }

            Movie savedMovie = movieService.saveMovie(existingMovie);
            return ResponseEntity.ok(new ApiResponse<>(true, "Movie updated successfully", savedMovie));
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(new ApiResponse<>(false, "An error occurred while updating the movie: " + e.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteMovie(@PathVariable UUID id) {
        try {
            movieService.deleteMovie(id);
            return ResponseEntity.ok(new ApiResponse<>(true, "Movie deleted successfully", null));
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "An error occurred while deleting the movie", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/detail/{slug}")
    public ResponseEntity<ApiResponse<Movie>> detailMovie(@PathVariable String slug) {
        try {
            Movie movie = movieService.getMovieBySlug(slug);
            if (movie == null) {
                return new ResponseEntity<>(new ApiResponse<>(false, "Movie not found", null), HttpStatus.NOT_FOUND);
            }
            return ResponseEntity.ok(new ApiResponse<>(true, "Movie details retrieved successfully", movie));
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "An error occurred while retrieving movie details", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private Map<String, Object> toPagedPayload(Page<Movie> page) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("items", page.getContent());
        payload.put("page", page.getNumber());
        payload.put("size", page.getSize());
        payload.put("totalItems", page.getTotalElements());
        payload.put("totalPages", page.getTotalPages());
        payload.put("hasNext", page.hasNext());
        payload.put("hasPrevious", page.hasPrevious());
        return payload;
    }

    private Pageable buildPageable(int page, int size, String sortBy, String sortDir) {
        Sort.Direction direction = "asc".equalsIgnoreCase(sortDir) ? Sort.Direction.ASC : Sort.Direction.DESC;
        String safeSortBy = (sortBy == null || sortBy.isBlank()) ? "createdAt" : sortBy;

        if (!ALLOWED_SORT_FIELDS.contains(safeSortBy)) {
            throw new IllegalArgumentException("Invalid sortBy. Allowed values: " + ALLOWED_SORT_FIELDS);
        }

        return PageRequest.of(Math.max(page, 0), Math.max(size, 1), Sort.by(direction, safeSortBy));
    }

    private CompletableFuture<String> resolveMediaAsync(MultipartFile file, String mediaUrl, String existingUrl, String mediaType) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                if (file != null && !file.isEmpty()) {
                    return cloudinaryService.uploadFile(file);
                }

                if (mediaUrl == null || mediaUrl.trim().isEmpty() || mediaUrl.equals(existingUrl)) {
                    return existingUrl;
                }

                // Skip re-upload when URL is already hosted by Cloudinary
                if (mediaUrl.contains("res.cloudinary.com")) {
                    return mediaUrl;
                }

                return cloudinaryService.uploadFromUrl(mediaUrl);
            } catch (Exception e) {
                throw new CompletionException(new RuntimeException("Failed to upload " + mediaType + ": " + e.getMessage(), e));
            }
        });
    }
}