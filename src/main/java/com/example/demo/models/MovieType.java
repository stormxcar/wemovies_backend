package com.example.demo.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovieType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long movie_type_id;
    private String type_name;

    public String getType_name() {
        return type_name;
    }

    public void setType_name(String type_name) {
        this.type_name = type_name;
    }

    public Long getMovie_type_id() {
        return movie_type_id;
    }

    public void setMovie_type_id(Long movie_type_id) {
        this.movie_type_id = movie_type_id;
    }
}
