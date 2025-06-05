package com.example.library_system.service;

import com.example.library_system.dto.LoanDTO;
import com.example.library_system.dto.UserDTO;
import com.example.library_system.entity.Loan;
import com.example.library_system.entity.User;
import com.example.library_system.repository.LoanRepository;
import com.example.library_system.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
@Validated
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LoanRepository loanRepository;

    public ResponseEntity<UserDTO> getUserByEmail(String email) {
        try {
            // Validate input
            if (email == null || email.trim().isEmpty()) {
                throw new IllegalArgumentException("Email cannot be blank");
            }
            Optional<UserDTO> userDTO = userRepository.findByEmail(email)
                    .map(this::convertToDTO);
            return userDTO.map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    public ResponseEntity<UserDTO> createUser(UserDTO userDTO) {
        try {
            // Validate input
            if (userDTO == null) {
                throw new IllegalArgumentException("UserDTO cannot be null");
            }
            if (userDTO.getFirstName() == null || userDTO.getFirstName().trim().isEmpty()) {
                throw new IllegalArgumentException("First name cannot be blank");
            }
            if (userDTO.getLastName() == null || userDTO.getLastName().trim().isEmpty()) {
                throw new IllegalArgumentException("Last name cannot be blank");
            }
            if (userDTO.getEmail() == null || userDTO.getEmail().trim().isEmpty()) {
                throw new IllegalArgumentException("Email cannot be blank");
            }
            // Check if email is already in use
            if (userRepository.findByEmail(userDTO.getEmail()).isPresent()) {
                throw new IllegalArgumentException("Email already in use: " + userDTO.getEmail());
            }
            User user = new User();
            user.setFirstName(userDTO.getFirstName());
            user.setLastName(userDTO.getLastName());
            user.setEmail(userDTO.getEmail());
            user.setPassword("defaultPassword"); // Handle securely in production
            user.setRegistrationDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            User savedUser = userRepository.save(user);
            return ResponseEntity.ok(convertToDTO(savedUser));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    public ResponseEntity<List<LoanDTO>> getUserLoans(Long userId) {
        try {
            // Validate input
            if (userId == null || userId <= 0) {
                throw new IllegalArgumentException("User ID must be positive");
            }
            // Validate user exists
            userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
            List<LoanDTO> loans = loanRepository.findByUserId(userId).stream()
                    .map(this::convertToLoanDTO)
                    .toList();
            return ResponseEntity.ok(loans);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    private UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setEmail(user.getEmail());
        dto.setRegistrationDate(user.getRegistrationDate());
        return dto;
    }

    private LoanDTO convertToLoanDTO(Loan loan) {
        LoanDTO dto = new LoanDTO();
        dto.setId(loan.getId());
        dto.setUserId(loan.getUserId());
        dto.setBookId(loan.getBookId());
        dto.setBorrowedDate(loan.getBorrowedDate());
        dto.setDueDate(loan.getDueDate());
        dto.setReturnedDate(loan.getReturnedDate());
        return dto;
    }
}