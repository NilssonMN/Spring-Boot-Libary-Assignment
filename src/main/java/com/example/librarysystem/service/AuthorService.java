package com.example.librarysystem.service;

import com.example.librarysystem.entity.Author;
import com.example.librarysystem.repository.AuthorRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthorService {

    private final AuthorRepository authorRepository;

    public AuthorService(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    public List<Author> getAllAuthors() {
        List<Author> authors = authorRepository.findAll();
        if (authors.isEmpty()) throw new IllegalArgumentException("No authors found");
        return authors;
    }

    public List<Author> getAuthorsByLastName(String lastName) {
        if (lastName == null || lastName.trim().isEmpty())
            throw new IllegalArgumentException("Last name cannot be null or empty");
        List<Author> authors = authorRepository.findByLastNameIgnoreCase(lastName);
        if (authors.isEmpty())
            throw new IllegalArgumentException("No authors found with last name: " + lastName);
        return authors;
    }

    public Author createAuthor(Author author) {
        if (author == null) throw new IllegalArgumentException("Author cannot be null");
        if (author.getFirstName() == null || author.getFirstName().trim().isEmpty())
            throw new IllegalArgumentException("First name cannot be null or empty");
        if (author.getLastName() == null || author.getLastName().trim().isEmpty())
            throw new IllegalArgumentException("Last name cannot be null or empty");
        if (author.getBirthYear() == null || author.getBirthYear() < 0)
            throw new IllegalArgumentException("Birth year cannot be null or negative");
        if (author.getNationality() == null || author.getNationality().trim().isEmpty())
            throw new IllegalArgumentException("Nationality cannot be null or empty");
        return authorRepository.save(author);
    }
}