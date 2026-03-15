package com.example.demo.services.Impls;

import com.example.demo.config.root.ResourceNotFoundException;
import com.example.demo.models.Movie;
import com.example.demo.repositories.HistoryRepository;
import com.example.demo.repositories.MovieRepository;
import com.example.demo.repositories.NotificationRepository;
import com.example.demo.repositories.ReviewRepository;
import com.example.demo.repositories.ViewingScheduleRepository;
import com.example.demo.repositories.WatchlistRepository;
import com.example.demo.services.MovieService;
import com.example.demo.services.SlugService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Service
public class MovieServiceImpl implements MovieService {
    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private SlugService slugService;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private HistoryRepository historyRepository;

    @Autowired
    private WatchlistRepository watchlistRepository;

    @Autowired
    private ViewingScheduleRepository viewingScheduleRepository;

    @Override
    public Page<Movie> getMoviesByPage(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        return movieRepository.findAll(pageable);
    }

    @Override
    public List<Movie> getAllMovies() {
        return movieRepository.findAll();
    }

    @Override
    public Page<Movie> getAllMovies(Pageable pageable) {
        return movieRepository.findAll(pageable);
    }

    @Override
    public Movie getMovieById(UUID id) {
        return movieRepository.findById(id).orElse(null);
    }

    @Override
    public Movie getMovieBySlug(String slug) {
        return movieRepository.findBySlug(slug);
    }

    @Override
    public Movie saveMovie(Movie movie) {
        Movie existing = null;
        if (movie.getId() != null) {
            existing = movieRepository.findById(movie.getId()).orElse(null);
        }

        boolean isNew = existing == null;
        boolean titleChanged = existing != null && existing.getTitle() != null
                && movie.getTitle() != null
                && !existing.getTitle().equals(movie.getTitle());

        // Generate slug for create or when title changed in update
        if (isNew || titleChanged || movie.getSlug() == null || movie.getSlug().trim().isEmpty()) {
            slugService.generateMovieSlug(movie);
        }
        return movieRepository.save(movie);
    }

    @Override
    public Movie updateMovie(UUID id, Movie updatedMovie) {
        Movie movie = movieRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Movie not found"));
//        updateMovie().setTitle(updatedMovie.getTitle());
        movie.setTitle(updatedMovie.getTitle());
        movie.setTitleByLanguage(updatedMovie.getTitleByLanguage());
        movie.setStatus(updatedMovie.getStatus());
        movie.setDirector(updatedMovie.getDirector());
        movie.setDuration(updatedMovie.getDuration());
        movie.setDescription(updatedMovie.getDescription());
        movie.setRelease_year(updatedMovie.getRelease_year());
        movie.setTrailer(updatedMovie.getTrailer());
        movie.setQuality(updatedMovie.getQuality());
        movie.setVietSub(updatedMovie.isVietSub());
        movie.setLink(updatedMovie.getLink());
        movie.setViews(updatedMovie.getViews());
        movie.setHot(updatedMovie.isHot());
        movie.setActorsSet(updatedMovie.getActorsSet());
        movie.setTotalEpisodes(updatedMovie.getTotalEpisodes());
        movie.setEpisodeLinks(updatedMovie.getEpisodeLinks());

        movie.setThumb_url(updatedMovie.getThumb_url());
        movie.setCountry(updatedMovie.getCountry());
        movie.setMovieTypes(updatedMovie.getMovieTypes());
        movie.setMovieCategories(updatedMovie.getMovieCategories());

        // Cập nhật các thuộc tính khác
        return movieRepository.save(movie);
    }

    @Override
    @Transactional
    public void deleteMovie(UUID id) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found"));

        // Remove dependent rows first to avoid FK constraint violations
        notificationRepository.deleteByRelatedMovieId(id);
        reviewRepository.deleteByMovieId(id);
        historyRepository.deleteByMovieId(id);
        watchlistRepository.deleteByMovieId(id);
        viewingScheduleRepository.deleteByMovieId(id);

        // Clear join-table relations explicitly before delete
        movie.getMovieTypes().clear();
        movie.getMovieCategories().clear();
        movieRepository.save(movie);

        movieRepository.delete(movie);
    }

    @Override
    public int countMovies() {
        return (int)movieRepository.count();
    }

    @Override
    public List<Movie> getMoviesByCategory(String categoryName) {
        return movieRepository.getMoviesByCategory(categoryName);
    }

    @Override
    public Page<Movie> getMoviesByCategory(String categoryName, Pageable pageable) {
        return movieRepository.getMoviesByCategory(categoryName, pageable);
    }

    @Override
    public List<Movie> searchMovie(String keyword) {
        return movieRepository.searchMovie(keyword);
    }

    @Override
    public Page<Movie> searchMovie(String keyword, Pageable pageable) {
        return movieRepository.searchMovie(keyword, pageable);
    }

    @Override
    public List<String> getEpisodeLinks(UUID movieId) {
        Optional<Movie> movieOptional = movieRepository.findById(movieId);
        if (movieOptional.isPresent()) {
            Movie movie = movieOptional.get();
            if (movie.getEpisodeLinks() != null) {
                return Arrays.asList(movie.getEpisodeLinks().split(","));
            }
        }
        return Collections.emptyList();
    }

    @Override
    public List<Movie> getMovieByHot(boolean isHot) {
        return movieRepository.getMovieByHot(isHot);
    }

    @Override
    public Page<Movie> getMovieByHot(boolean isHot, Pageable pageable) {
        return movieRepository.getMovieByHot(isHot, pageable);
    }

    @Override
    public List<Movie> getMoviesByCategoryId(UUID categoryId) {
        return movieRepository.getMoviesByCategoryId(categoryId);
    }

    @Override
    public Page<Movie> getMoviesByCategoryId(UUID categoryId, Pageable pageable) {
        return movieRepository.getMoviesByCategoryId(categoryId, pageable);
    }

    @Override
    public List<Movie> getMoviesByCountryId(UUID countryId) {
        return movieRepository.getMoviesByCountryId(countryId);
    }

    @Override
    public Page<Movie> getMoviesByCountryId(UUID countryId, Pageable pageable) {
        return movieRepository.getMoviesByCountryId(countryId, pageable);
    }

    @Override
    public List<Movie> findMoviesByCountryAndCategory(String countryName, String categoryName) {
        return movieRepository.findMoviesByCountryAndCategory(countryName, categoryName);
    }

    @Override
    public Page<Movie> findMoviesByCountryAndCategory(String countryName, String categoryName, Pageable pageable) {
        return movieRepository.findMoviesByCountryAndCategory(countryName, categoryName, pageable);
    }
    
    @Override
    public List<Movie> getMoviesByMovieType(String movieTypeName) {
        return movieRepository.getMoviesByMovieType(movieTypeName);
    }

    @Override
    public Page<Movie> getMoviesByMovieType(String movieTypeName, Pageable pageable) {
        return movieRepository.getMoviesByMovieType(movieTypeName, pageable);
    }
    
    @Override
    public List<Movie> getMoviesByMovieTypeId(UUID movieTypeId) {
        return movieRepository.getMoviesByMovieTypeId(movieTypeId);
    }

    @Override
    public Page<Movie> getMoviesByMovieTypeId(UUID movieTypeId, Pageable pageable) {
        return movieRepository.getMoviesByMovieTypeId(movieTypeId, pageable);
    }
}
