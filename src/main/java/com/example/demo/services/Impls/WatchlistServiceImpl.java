package com.example.demo.services.Impls;

import com.example.demo.models.Movie;
import com.example.demo.models.Watchlist;
import com.example.demo.models.auth.User;
import com.example.demo.repositories.MovieRepository;
import com.example.demo.repositories.WatchlistRepository;
import com.example.demo.repositories.auth.UserRepository;
import com.example.demo.services.WatchlistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class WatchlistServiceImpl implements WatchlistService {

    @Autowired
    private WatchlistRepository watchlistRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Override
    public void addToWatchlist(String email, String movieId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Movie movie = movieRepository.findById(UUID.fromString(movieId))
                .orElseThrow(() -> new RuntimeException("Movie not found"));

        if (watchlistRepository.findByUserAndMovie(user, movie).isPresent()) {
            throw new RuntimeException("Movie already in watchlist");
        }

        Watchlist watchlist = new Watchlist();
        watchlist.setUser(user);
        watchlist.setMovie(movie);
        watchlist.setAddedAt(LocalDateTime.now());
        watchlistRepository.save(watchlist);
    }

    @Override
    public void removeFromWatchlist(String email, String movieId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Movie movie = movieRepository.findById(UUID.fromString(movieId))
                .orElseThrow(() -> new RuntimeException("Movie not found"));

        Watchlist watchlist = watchlistRepository.findByUserAndMovie(user, movie)
                .orElseThrow(() -> new RuntimeException("Movie not in watchlist"));
        watchlistRepository.delete(watchlist);
    }

    @Override
    public List<Watchlist> getWatchlist(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return watchlistRepository.findByUser(user);
    }

    @Override
    public boolean isInWatchlist(String email, String movieId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Movie movie = movieRepository.findById(UUID.fromString(movieId))
                .orElseThrow(() -> new RuntimeException("Movie not found"));
        return watchlistRepository.findByUserAndMovie(user, movie).isPresent();
    }
}