package com.example.librarysystem.service;

import com.example.librarysystem.dto.UserDTO;
import com.example.librarysystem.entity.User;
import com.example.librarysystem.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserDTO getUserByEmail(String email) {
        if (email == null || email.trim().isEmpty())
            throw new IllegalArgumentException("Email cannot be null or empty");
        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new IllegalArgumentException("No user found with email: " + email));
        return toUserDTO(user);
    }

    public UserDTO createUser(User user) {
        if (user == null) throw new IllegalArgumentException("User cannot be null");

        // Validate firstName
        if (user.getFirstName() == null || user.getFirstName().trim().isEmpty())
            throw new IllegalArgumentException("First name cannot be null or empty");
        if (user.getFirstName().length() < 2 || user.getFirstName().length() > 50)
            throw new IllegalArgumentException("First name must be between 2 and 50 characters");

        // Validate lastName
        if (user.getLastName() == null || user.getLastName().trim().isEmpty())
            throw new IllegalArgumentException("Last name cannot be null or empty");
        if (user.getLastName().length() < 2 || user.getLastName().length() > 50)
            throw new IllegalArgumentException("Last name must be between 2 and 50 characters");

        // Validate email
        if (user.getEmail() == null || user.getEmail().trim().isEmpty())
            throw new IllegalArgumentException("Email cannot be null or empty");
        if (!user.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$"))
            throw new IllegalArgumentException("Invalid email format");

        // Validate password
        if (user.getPassword() == null || user.getPassword().trim().isEmpty())
            throw new IllegalArgumentException("Password cannot be null or empty");
        if (!isValidPassword(user.getPassword()))
            throw new IllegalArgumentException("Password must be at least 8 characters long and contain both letters and numbers");

        // Check for duplicate email
        if (userRepository.findByEmailIgnoreCase(user.getEmail()).isPresent())
            throw new IllegalArgumentException("Email already exists: " + user.getEmail());

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole("USER");
        User savedUser = userRepository.save(user);
        return toUserDTO(savedUser);
    }

    private boolean isValidPassword(String password) {
        if (password.length() < 8) return false;
        boolean hasLetter = password.matches(".*[a-zA-Z].*");
        boolean hasNumber = password.matches(".*[0-9].*");
        return hasLetter && hasNumber;
    }

    private UserDTO toUserDTO(User user) {
        return new UserDTO(
                user.getUserId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getRegistrationDate()
        );
    }
}