package com.example.demo;

import com.example.demo.models.auth.Role;
import com.example.demo.models.auth.User;
import com.example.demo.repositories.auth.RoleRepository;
import com.example.demo.repositories.auth.UserRepository;
import com.example.demo.repositories.auth.LoginAttemptRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;
import java.time.LocalDateTime;

import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.beans.factory.annotation.Autowired;

@SpringBootApplication
@EnableJpaAuditing
@EnableAsync
@EnableScheduling
public class DemoApplication {

    @Autowired
    private LoginAttemptRepository loginAttemptRepository;

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

    @Scheduled(fixedRate = 3600000) // Run every hour (3600000 milliseconds)
    public void cleanupExpiredLoginAttempts() {
        LocalDateTime cutoffDate = LocalDateTime.now().minusHours(1);
        loginAttemptRepository.deleteOldAttempts(cutoffDate);
        System.out.println("Cleaned up expired login attempts older than 1 hour");
    }

}
