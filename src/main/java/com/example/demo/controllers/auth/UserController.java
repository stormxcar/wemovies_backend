package com.example.demo.controllers.auth;

import com.example.demo.dto.UserDTO;
import com.example.demo.models.auth.User;
import com.example.demo.repositories.auth.UserRepository;
import com.example.demo.services.Impls.CloudinaryService;
import com.example.demo.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private CloudinaryService cloudinaryService;

    @Autowired
    private UserRepository userRepository;


    public UserController(UserService userService, UserRepository userRepository, CloudinaryService cloudinaryService) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.cloudinaryService = cloudinaryService;
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userRepository.findAll();
        return ResponseEntity.ok(users);
    }

    // get user & admin by id
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        User user = userService.findById(id);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/admin/{id}")
    public ResponseEntity<UserDTO> getUserDTOById(@PathVariable Long id) {
        UserDTO user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/role/{id}")
    public ResponseEntity<String> getUserRoleById(@PathVariable Long id) {
        String role = userService.getUserRoleById(id);
        return ResponseEntity.ok(role);
    }


    @PutMapping("/{id}/update-profile")
    public ResponseEntity<User> updateUserProfile(
            @PathVariable Long id,
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam(value = "fullName", required = false) String fullName,
            @RequestParam(value = "address", required = false) String address,
            @RequestParam(value = "dateOfBirth", required = false) String dateOfBirth,
            @RequestParam(value = "gender", required = false) String gender) {
        try {
            String avatarUrl = null;
            if (file != null) {
                avatarUrl = cloudinaryService.uploadFile(file);
            }
            User updatedUser = userService.updateUserProfile(id, fullName, address, dateOfBirth, gender, avatarUrl);
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(null);
        }
    }

}
