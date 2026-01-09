package com.example.demo.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.AssertTrue;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(name = "movie_type", indexes = {
    @Index(name = "idx_movie_type_slug", columnList = "slug", unique = true)
})
public class MovieType extends BaseEntity {
    private String name;

    @Column(unique = true)
    private String slug;
}
