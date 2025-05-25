package com.example.demo.services;

import com.example.demo.dto.UserDTO;
import com.example.demo.models.auth.User;

import java.util.List;

public interface UserService {
    User findByUsername(String username);
    User findById(Long id);

    // update user information
    User updateUserProfile(Long id, String fullName, String address, String dateOfBirth, String gender, String avatarUrl);


    // User DTO
    UserDTO getUserById(Long id);

    String getUserRoleById(Long id);
}
