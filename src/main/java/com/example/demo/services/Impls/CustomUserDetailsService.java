package com.example.demo.services.Impls;

import com.example.demo.models.auth.User;
import com.example.demo.repositories.auth.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy người dùng với email: " + email));

        Set<GrantedAuthority> authorities = new HashSet<>();
        String roleName = user.getRole() != null ? user.getRole().getRoleName() : null;
        if (roleName != null && !roleName.isBlank()) {
            authorities.add(new SimpleGrantedAuthority(roleName));
            if (!roleName.startsWith("ROLE_")) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + roleName));
            }
        }

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassWord(),
            authorities
        );
    }
}
