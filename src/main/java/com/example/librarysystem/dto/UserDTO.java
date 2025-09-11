package com.example.librarysystem.dto;

import java.time.LocalDateTime;

public class UserDTO {

    private Long userId;
    private String firstName;
    private String lastName;
    private String email;
    private LocalDateTime registrationDate;

    public UserDTO() {}

    public UserDTO(Long userId, String firstName, String lastName, String email, LocalDateTime registrationDate) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.registrationDate = registrationDate;
    }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public LocalDateTime getRegistrationDate() { return registrationDate; }
    public void setRegistrationDate(LocalDateTime registrationDate) { this.registrationDate = registrationDate; }
}