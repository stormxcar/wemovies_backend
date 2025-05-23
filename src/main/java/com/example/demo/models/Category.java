package com.example.demo.models;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long category_id;
    public String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCategory_id(Long category_id) {
        this.category_id = category_id;
    }

    public Long getCategory_id() {
        return category_id;
    }
}
