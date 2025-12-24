package com.example.demo;

import com.example.demo.models.auth.Role;
import com.example.demo.models.auth.User;
import com.example.demo.repositories.auth.RoleRepository;
import com.example.demo.repositories.auth.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @Bean
    public CommandLineRunner initData(UserRepository userRepository, RoleRepository roleRepository) {
        return args -> {
            // Create ADMIN role if not exists
            Optional<Role> adminRoleOpt = roleRepository.findByRoleName("ADMIN");
            Role adminRole;
            if (adminRoleOpt.isEmpty()) {
                adminRole = new Role();
                adminRole.setRoleName("ADMIN");
                adminRole.setDescription("Administrator role");
                roleRepository.save(adminRole);
            } else {
                adminRole = adminRoleOpt.get();
            }

            // Create USER role if not exists
            Optional<Role> userRoleOpt = roleRepository.findByRoleName("USER");
            Role userRole;
            if (userRoleOpt.isEmpty()) {
                userRole = new Role();
                userRole.setRoleName("USER");
                userRole.setDescription("User role");
                roleRepository.save(userRole);
            } else {
                userRole = userRoleOpt.get();
            }

            // Create admin user if not exists
            Optional<User> adminUserOpt = userRepository.findByUserName("admin");
            if (adminUserOpt.isEmpty()) {
                User adminUser = new User();
                adminUser.setUserName("admin");
                adminUser.setPassWord(new BCryptPasswordEncoder().encode("Admin@1234"));
                adminUser.setEmail("admin@example.com");
                adminUser.setRole(adminRole);
                adminUser.setIsActive(true);
                userRepository.save(adminUser);
                System.out.println("Admin user created: username=admin, password=admin");
            } else {
                System.out.println("Admin user already exists");
            }
        };
    }

}
