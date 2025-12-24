package com.example.demo.models;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private com.example.demo.models.auth.User user;

    @ManyToOne
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;

    @Column(name = "pause_time")
    private Double pauseTime;

    @Column(name = "viewed_at")
    private LocalDateTime viewedAt;


    public History(com.example.demo.models.auth.User user, Movie movie, Double pauseTime) {
        this.user = user;
        this.movie = movie;
        this.pauseTime = pauseTime;
        this.viewedAt = LocalDateTime.now();
    }
}
