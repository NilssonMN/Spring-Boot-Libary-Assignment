package com.example.librarysystem.controller;

import com.example.librarysystem.dto.BookWithDetailsDTO;
import com.example.librarysystem.entity.Book;
import com.example.librarysystem.service.BookService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    public List<BookWithDetailsDTO> getAllBooks() {
        return bookService.getAllBooks();
    }

    @GetMapping("/search")
    public List<BookWithDetailsDTO> searchBooks(
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "authorId", required = false) Long authorId) {
        return bookService.searchBooks(title, authorId);
    }

    @PostMapping
    public BookWithDetailsDTO createBook(@RequestBody Book book) {
        return bookService.createBook(book);
    }
}