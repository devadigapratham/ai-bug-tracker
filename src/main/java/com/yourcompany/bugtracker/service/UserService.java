package com.yourcompany.bugtracker.service;

import com.yourcompany.bugtracker.model.Role;
import com.yourcompany.bugtracker.model.User;
import com.yourcompany.bugtracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

     public List<User> findDevelopers() {
        // Example: Find users with DEVELOPER role. Adjust if Role is an entity.
        return userRepository.findAll().stream()
                .filter(user -> user.getRole() == Role.DEVELOPER)
                .toList();
     }

    @Transactional // Ensure atomicity
    public User registerUser(User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already exists");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        // Set default role if not provided? Or handle in controller/DTO
        if (user.getRole() == null) {
            user.setRole(Role.REPORTER); // Default role
        }
        user.setEnabled(true); // Enable user by default
        return userRepository.save(user);
    }

    // Add methods for update, delete, role changes etc. as needed
}
