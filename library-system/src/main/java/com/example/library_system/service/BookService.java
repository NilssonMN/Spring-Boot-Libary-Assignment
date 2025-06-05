package com.example.library_system.service;

import com.example.library_system.dto.AuthorDTO;
import com.example.library_system.dto.BookDTO;
import com.example.library_system.dto.BookWithDetailsDTO;
import com.example.library_system.entity.Book;
import com.example.library_system.repository.AuthorRepository;
import com.example.library_system.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Service
@Validated
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorRepository authorRepository;

    public List<BookDTO> getAllBooks() {
        return bookRepository.findAll().stream()
                .map(this::convertToDTO)
                .toList();
    }

    public List<BookDTO> searchBooks(String title, Long authorId) {
        return bookRepository.searchBooks(title, authorId).stream()
                .map(this::convertToDTO)
                .toList();
    }

    public ResponseEntity<BookDTO> createBook(BookDTO bookDTO) {
        try {
            // Validate input
            if (bookDTO.getTitle() == null || bookDTO.getTitle().trim().isEmpty()) {
                throw new IllegalArgumentException("Title cannot be blank");
            }
            if (bookDTO.getAvailableCopies() == null || bookDTO.getAvailableCopies() <= 0) {
                throw new IllegalArgumentException("Available copies must be positive");
            }
            if (bookDTO.getTotalCopies() == null || bookDTO.getTotalCopies() <= 0) {
                throw new IllegalArgumentException("Total copies must be positive");
            }
            if (bookDTO.getAuthorId() == null || bookDTO.getAuthorId() <= 0) {
                throw new IllegalArgumentException("Author ID must be positive");
            }
            // Validate author exists
            authorRepository.findById(bookDTO.getAuthorId())
                    .orElseThrow(() -> new IllegalArgumentException("Author not found with ID: " + bookDTO.getAuthorId()));

            Book book = new Book();
            book.setTitle(bookDTO.getTitle());
            book.setPublicationYear(bookDTO.getPublicationYear());
            book.setAvailableCopies(bookDTO.getAvailableCopies());
            book.setTotalCopies(bookDTO.getTotalCopies());
            book.setAuthorId(bookDTO.getAuthorId());
            Book savedBook = bookRepository.save(book);
            return ResponseEntity.ok(convertToDTO(savedBook));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    public List<BookWithDetailsDTO> getAllBooksWithDetails() {
        return bookRepository.findAll().stream()
                .map(this::convertToDetailsDTO)
                .toList();
    }

    private BookDTO convertToDTO(Book book) {
        BookDTO dto = new BookDTO();
        dto.setId(book.getId());
        dto.setTitle(book.getTitle());
        dto.setPublicationYear(book.getPublicationYear());
        dto.setAvailableCopies(book.getAvailableCopies());
        dto.setTotalCopies(book.getTotalCopies());
        dto.setAuthorId(book.getAuthorId());
        return dto;
    }

    private BookWithDetailsDTO convertToDetailsDTO(Book book) {
        BookWithDetailsDTO dto = new BookWithDetailsDTO();
        dto.setId(book.getId());
        dto.setTitle(book.getTitle());
        dto.setPublicationYear(book.getPublicationYear());
        dto.setAvailableCopies(book.getAvailableCopies());
        dto.setTotalCopies(book.getTotalCopies());

        AuthorDTO authorDTO = authorRepository.findById(book.getAuthorId())
                .map(author -> {
                    AuthorDTO adto = new AuthorDTO();
                    adto.setId(author.getId());
                    adto.setFirstName(author.getFirstName());
                    adto.setLastName(author.getLastName());
                    adto.setBirthYear(author.getBirthYear());
                    adto.setNationality(author.getNationality());
                    return adto;
                })
                .orElse(null);
        dto.setAuthor(authorDTO);
        return dto;
    }
}