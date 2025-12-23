package com.example.demo.models;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
@Data
@Table(name = "history")
public class History {
     @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "movie_id", nullable = false)
    private String movieId;

    @Column(name = "pause_time")
    private Double pauseTime;

    @Column(name = "last_watched")
    private LocalDateTime lastWatched;


    public History(String userId, String movieId, Double pauseTime) {
        this.userId = userId;
        this.movieId = movieId;
        this.pauseTime = pauseTime;
        this.lastWatched = LocalDateTime.now();
    }
}
