package com.example.demo.services;

import com.example.demo.dto.response.UserDTO;
import com.example.demo.models.auth.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
    User findByUsername(String username);
    User findById(UUID id);

    // update user information
    User updateUserProfile(UUID id, String fullName, String address, String dateOfBirth, String gender, String avatarUrl);


    // User DTO
    UserDTO getUserById(UUID id);

    String getUserRoleById(UUID id);
}
