package com.example.demo.services;

import com.example.demo.models.Watchlist;
import com.example.demo.models.auth.User;

import java.util.List;

public interface WatchlistService {
    void addToWatchlist(String email, String movieId);
    void removeFromWatchlist(String email, String movieId);
    List<Watchlist> getWatchlist(String email);
    boolean isInWatchlist(String email, String movieId);
}