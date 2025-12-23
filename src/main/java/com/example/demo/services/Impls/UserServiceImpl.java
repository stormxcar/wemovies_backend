package com.example.demo.services.Impls;

import com.example.demo.dto.response.UserDTO;
import com.example.demo.exception.UserNotFoundException;
import com.example.demo.models.auth.Gender;
import com.example.demo.models.auth.User;
import com.example.demo.repositories.auth.UserRepository;
import com.example.demo.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;


    @Override
    public User findByUsername(String username) {
        return userRepository.findByUserName(username)
                .orElseThrow(() -> new UserNotFoundException("User not found with username: " + username));
    }

    @Override
    public User findById(UUID id) {
        return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found with id: " + id))
                ;
    }


    @Override
    public User updateUserProfile(UUID id, String fullName, String address, String dateOfBirth, String gender, String avatarUrl) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (fullName != null) {
            user.setFullName(fullName);
        }
        if (address != null) {
            user.setAddress(address);
        }
        if (dateOfBirth != null) {
            user.setDateOfBirth(Date.valueOf(dateOfBirth));
        }
        if (gender != null) {
            user.setGender(Gender.valueOf(gender.toUpperCase()));
        }
        if (avatarUrl != null) {
            user.setAvatar(avatarUrl);
        }

        user.setUpdateAt(LocalDateTime.now());
        return userRepository.save(user);
    }



    @Override
    public UserDTO getUserById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
        return mapToDTO(user);
    }

    private UserDTO mapToDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setUsername(user.getUserName());
        userDTO.setEmail(user.getEmail());
        userDTO.setFullName(user.getFullName());
        userDTO.setPhone(user.getPhoneNumber());
        userDTO.setAddress(user.getAddress());
        userDTO.setAvatar(user.getAvatar());
        userDTO.setRole(user.getRole().getRoleName());

        return userDTO;
    }

    @Override
    public String getUserRoleById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
        return user.getRole().getRoleName();
    }
}
