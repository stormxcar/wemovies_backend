package com.example.demo.models;


import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(name = "country", indexes = {
    @Index(name = "idx_country_slug", columnList = "slug", unique = true)
})
public class Country extends BaseEntity {
    private String name;
    
    @Column(unique = true)
    private String slug;
}
