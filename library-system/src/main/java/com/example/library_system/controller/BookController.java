package com.example.library_system.controller;

import com.example.library_system.dto.BookDTO;
import com.example.library_system.dto.BookWithDetailsDTO;
import com.example.library_system.service.BookService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@RestController
@RequestMapping("/books")
public class BookController {

    @Autowired
    private BookService bookService;

    //Lista alla böcker - http://localhost:8080/books
    @GetMapping
    public List<BookDTO> getAllBooks() {
        return bookService.getAllBooks();
    }


    /*http://localhost:8080/books/search?authorId=3 eller
     http://localhost:8080/books/search?title=The Ice Princess
    */
    @GetMapping("/search")
    public List<BookDTO> searchBooks(@RequestParam(value = "title", required = false) String title,
                                     @RequestParam(value = "authorId", required = false) Long authorId) {
        return bookService.searchBooks(title, authorId);
    }


    /*
    Skapa ny book http://localhost:8080/books
    {
    "title": "New Book",
    "publicationYear": 2023,
    "availableCopies": 5,
    "totalCopies": 5,
    "authorId": 1
    }   */

    @PostMapping
    public ResponseEntity<BookDTO> createBook(@Valid @RequestBody BookDTO bookDTO) {
        return bookService.createBook(bookDTO);
    }


    //med Author-info - http://localhost:8080/books/details
    @GetMapping("/details")
    public List<BookWithDetailsDTO> getAllBooksWithDetails() {
        return bookService.getAllBooksWithDetails();
    }

}