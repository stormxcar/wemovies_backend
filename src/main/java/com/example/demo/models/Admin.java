package com.example.demo.models;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Admin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long admin_id;
    private String username;
    private String password;
    private String email;
    private String role;

    public String getPassword() {
        return password;
    }
}

