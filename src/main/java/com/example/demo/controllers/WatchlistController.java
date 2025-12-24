package com.example.demo.controllers;

import com.example.demo.models.Watchlist;
import com.example.demo.services.WatchlistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/watchlist")
public class WatchlistController {

    @Autowired
    private WatchlistService watchlistService;

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/add/{movieId}")
    public ResponseEntity<String> addToWatchlist(@PathVariable String movieId, @AuthenticationPrincipal UserDetails userDetails) {
        watchlistService.addToWatchlist(userDetails.getUsername(), movieId);
        return ResponseEntity.ok("Added to watchlist");
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/remove/{movieId}")
    public ResponseEntity<String> removeFromWatchlist(@PathVariable String movieId, @AuthenticationPrincipal UserDetails userDetails) {
        watchlistService.removeFromWatchlist(userDetails.getUsername(), movieId);
        return ResponseEntity.ok("Removed from watchlist");
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping()
    public ResponseEntity<List<Watchlist>> getWatchlist(@AuthenticationPrincipal UserDetails userDetails) {
        List<Watchlist> watchlist = watchlistService.getWatchlist(userDetails.getUsername());
        return ResponseEntity.ok(watchlist);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/check/{movieId}")
    public ResponseEntity<Boolean> isInWatchlist(@PathVariable String movieId, @AuthenticationPrincipal UserDetails userDetails) {
        boolean inWatchlist = watchlistService.isInWatchlist(userDetails.getUsername(), movieId);
        return ResponseEntity.ok(inWatchlist);
    }
}