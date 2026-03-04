package com.example.demo.services.Impls;

import com.example.demo.dto.response.UserDTO;
import com.example.demo.exception.UserNotFoundException;
import com.example.demo.enums.Gender;
import com.example.demo.models.auth.Role;
import com.example.demo.models.auth.User;
import com.example.demo.repositories.auth.RoleRepository;
import com.example.demo.repositories.auth.UserRepository;
import com.example.demo.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


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

        user.setUpdatedAt(LocalDateTime.now());
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

    @Override
    public User createUserByAdmin(Map<String, Object> request) {
        String userName = request.get("userName") != null ? request.get("userName").toString().trim() : null;
        String email = request.get("email") != null ? request.get("email").toString().trim() : null;
        String passWord = request.get("passWord") != null ? request.get("passWord").toString() : null;
        String roleName = request.get("roleName") != null ? request.get("roleName").toString().trim().toUpperCase() : "USER";

        if (userName == null || userName.isBlank()) {
            throw new IllegalArgumentException("userName là bắt buộc");
        }
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("email là bắt buộc");
        }
        if (passWord == null || passWord.isBlank()) {
            throw new IllegalArgumentException("passWord là bắt buộc");
        }

        if (userRepository.findByUserName(userName).isPresent()) {
            throw new IllegalArgumentException("userName đã tồn tại");
        }
        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("email đã tồn tại");
        }

        Role role = roleRepository.findByRoleName(roleName)
                .orElseThrow(() -> new IllegalArgumentException("Role không tồn tại: " + roleName));

        User user = new User();
        user.setUserName(userName);
        user.setEmail(email);
        user.setPassWord(passwordEncoder.encode(passWord));
        user.setRole(role);
        user.setFullName(request.get("fullName") != null ? request.get("fullName").toString().trim() : null);
        user.setPhoneNumber(request.get("phoneNumber") != null ? request.get("phoneNumber").toString().trim() : null);
        user.setAddress(request.get("address") != null ? request.get("address").toString().trim() : null);
        user.setIsActive(true); // Admin tạo thủ công => xem như đã xác thực
        user.setCreatedAt(LocalDateTime.now());

        return userRepository.save(user);
    }

    @Override
    public User setUserLockStatus(UUID id, boolean locked) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));

        user.setIsActive(!locked);
        user.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }
}
