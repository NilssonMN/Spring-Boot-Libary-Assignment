package com.example.library_system.service;

import com.example.library_system.dto.AuthorDTO;
import com.example.library_system.entity.Author;
import com.example.library_system.repository.AuthorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Service
@Validated
public class AuthorService {

    @Autowired
    private AuthorRepository authorRepository;

    public ResponseEntity<List<AuthorDTO>> getAllAuthors() {
        try {
            List<AuthorDTO> authors = authorRepository.findAll().stream()
                    .map(this::convertToDTO)
                    .toList();
            return ResponseEntity.ok(authors);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    public ResponseEntity<List<AuthorDTO>> getAuthorsByLastName(String lastName) {
        try {
            // Validate input
            if (lastName == null || lastName.trim().isEmpty()) {
                throw new IllegalArgumentException("Last name cannot be blank");
            }
            List<AuthorDTO> authors = authorRepository.findByLastName(lastName).stream()
                    .map(this::convertToDTO)
                    .toList();
            return ResponseEntity.ok(authors);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    public ResponseEntity<AuthorDTO> createAuthor(AuthorDTO authorDTO) {
        try {
            // Validate input
            if (authorDTO == null) {
                throw new IllegalArgumentException("AuthorDTO cannot be null");
            }
            if (authorDTO.getFirstName() == null || authorDTO.getFirstName().trim().isEmpty()) {
                throw new IllegalArgumentException("First name cannot be blank");
            }
            if (authorDTO.getLastName() == null || authorDTO.getLastName().trim().isEmpty()) {
                throw new IllegalArgumentException("Last name cannot be blank");
            }
            if (authorDTO.getBirthYear() != null && authorDTO.getBirthYear() <= 0) {
                throw new IllegalArgumentException("Birth year must be positive");
            }
            Author author = new Author();
            author.setFirstName(authorDTO.getFirstName());
            author.setLastName(authorDTO.getLastName());
            author.setBirthYear(authorDTO.getBirthYear());
            author.setNationality(authorDTO.getNationality());
            Author savedAuthor = authorRepository.save(author);
            return ResponseEntity.ok(convertToDTO(savedAuthor));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    private AuthorDTO convertToDTO(Author author) {
        AuthorDTO dto = new AuthorDTO();
        dto.setId(author.getId());
        dto.setFirstName(author.getFirstName());
        dto.setLastName(author.getLastName());
        dto.setBirthYear(author.getBirthYear());
        dto.setNationality(author.getNationality());
        return dto;
    }
}