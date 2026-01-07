package com.example.demo.controllers;

import com.example.demo.dto.response.ApiResponse;
import com.example.demo.dto.response.MovieDto;
import com.example.demo.models.*;
import com.example.demo.services.Impls.CloudinaryService;
import com.example.demo.services.CategoryService;
import com.example.demo.services.CountryService;
import com.example.demo.services.MovieService;
import com.example.demo.services.MovieTypeSevice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.Year;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/movies")
public class MovieController {
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

    @GetMapping()
    public ResponseEntity<ApiResponse<List<Movie>>> movies() {
        try {
            List<Movie> movies = movieService.getAllMovies();
            if (movies.isEmpty()) {
                return new ResponseEntity<>(new ApiResponse<>(true, "No movies found", null), HttpStatus.NO_CONTENT);
            }
            return ResponseEntity.ok(new ApiResponse<>(true, "Movies retrieved successfully", movies));
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

    @GetMapping("/addPage")
    public ResponseEntity<ApiResponse<Object>> addPage() {
        try {
            Object response = new Object() {
                public List<Movie> movies = movieService.getAllMovies();
                public long countMovie = movieService.countMovies();
                public List<Category> categories = categoryService.getAllCategory();
                public List<MovieType> types = movieTypeSevice.getAllMovieTypes();
                public List<Country> countries = countryService.getAllCountries();
            };
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
                                                       @RequestParam("titleByLanguage") String titleByLanguage,
                                                       @RequestParam(value = "totalEpisodes", required = false) Integer totalEpisodes,
                                                       @RequestParam("trailer") String trailer,
                                                       @RequestParam("vietSub") boolean vietSub,
                                                       @RequestParam("actors") String[] actors,
                                                       @RequestParam(value = "episodeLinks", required = false) List<String> episodeLinks,
                                                       @RequestParam("countryId") UUID countryId,
                                                       @RequestParam("movieTypeIds") List<UUID> movieTypeIds,
                                                       @RequestParam("categoryIds") List<UUID> categoryIds) {
        try {
            // Create Movie object from parameters
            Movie movie = new Movie();
            movie.setTitle(title);
            // Handle thumbnail upload
            String finalThumbUrl = null;
            try {
                if (thumbnailFile != null && !thumbnailFile.isEmpty()) {
                    // Upload file to Cloudinary
                    finalThumbUrl = cloudinaryService.uploadFile(thumbnailFile);
                } else if (thumbUrl != null && !thumbUrl.trim().isEmpty()) {
                    // Upload URL to Cloudinary
                    finalThumbUrl = cloudinaryService.uploadFromUrl(thumbUrl);
                }
            } catch (Exception e) {
                return new ResponseEntity<>(new ApiResponse<>(false, "Failed to upload thumbnail: " + e.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
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
            movie.setTitleByLanguage(titleByLanguage);
            movie.setTotalEpisodes(totalEpisodes);
            movie.setTrailer(trailer);
            movie.setVietSub(vietSub);

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
                                                          @RequestParam("titleByLanguage") String titleByLanguage,
                                                          @RequestParam(value = "totalEpisodes", required = false) Integer totalEpisodes,
                                                          @RequestParam("trailer") String trailer,
                                                          @RequestParam("vietSub") boolean vietSub,
                                                          @RequestParam("actors") String[] actors,
                                                          @RequestParam(value = "episodeLinks", required = false) List<String> episodeLinks,
                                                          @RequestParam("countryId") UUID countryId,
                                                          @RequestParam("movieTypeIds") List<UUID> movieTypeIds,
                                                          @RequestParam("categoryIds") List<UUID> categoryIds) {

        try {
            Movie existingMovie = movieService.getMovieById(id);
            if (existingMovie == null) {
                return new ResponseEntity<>(new ApiResponse<>(false, "Movie not found", null), HttpStatus.NOT_FOUND);
            }

            // Handle thumbnail upload
            String finalThumbUrl = existingMovie.getThumb_url(); // Keep existing if no new upload
            try {
                if (thumbnailFile != null && !thumbnailFile.isEmpty()) {
                    // Upload file to Cloudinary
                    finalThumbUrl = cloudinaryService.uploadFile(thumbnailFile);
                } else if (thumbUrl != null && !thumbUrl.trim().isEmpty() && !thumbUrl.equals(existingMovie.getThumb_url())) {
                    // Upload URL to Cloudinary only if it's different from current
                    finalThumbUrl = cloudinaryService.uploadFromUrl(thumbUrl);
                }
            } catch (Exception e) {
                return new ResponseEntity<>(new ApiResponse<>(false, "Failed to upload thumbnail: " + e.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
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
            existingMovie.setTitleByLanguage(titleByLanguage);
            existingMovie.setTotalEpisodes(totalEpisodes);
            existingMovie.setTrailer(trailer);
            existingMovie.setVietSub(vietSub);

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

    @GetMapping("/detail/{id}")
    public ResponseEntity<ApiResponse<Movie>> detailMovie(@PathVariable UUID id) {
        try {
            Movie movie = movieService.getMovieById(id);
            return ResponseEntity.ok(new ApiResponse<>(true, "Movie details retrieved successfully", movie));
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "An error occurred while retrieving movie details", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}