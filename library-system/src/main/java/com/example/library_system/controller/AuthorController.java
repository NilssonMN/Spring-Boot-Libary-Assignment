package com.example.library_system.controller;
import com.example.library_system.dto.AuthorDTO;
import com.example.library_system.service.AuthorService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@RestController
@RequestMapping("/authors")
public class AuthorController {

    @Autowired
    private AuthorService authorService;

    // Lista alla författare - http://localhost:8080/authors
    @GetMapping
    public ResponseEntity<List<AuthorDTO>> getAllAuthors() {
        return authorService.getAllAuthors();
    }

    //Hämta författare via efternamn - http://localhost:8080/authors/name/Larsson
    @GetMapping("/name/{lastName}")
    public ResponseEntity<List<AuthorDTO>> getAuthorsByLastName(@PathVariable String lastName) {
        return authorService.getAuthorsByLastName(lastName);
    }


    /*Skapa ny författare - http://localhost:8080/authors
    {
    "firstName": "New",
    "lastName": "Test",
    "birthYear": 2001,
    "nationality": "Swedish"
    }
     */
    @PostMapping
    public ResponseEntity<AuthorDTO> createAuthor(@Valid @RequestBody AuthorDTO authorDTO) {
        return authorService.createAuthor(authorDTO);
    }
}