package com.example.demo.models;


import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(name = "category", indexes = {
    @Index(name = "idx_category_slug", columnList = "slug", unique = true)
})
public class Category extends BaseEntity {
    private String name;

    @Column(unique = true)
    private String slug;
}
