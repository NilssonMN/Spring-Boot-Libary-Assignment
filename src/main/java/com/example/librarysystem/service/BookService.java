package com.example.librarysystem.service;

import com.example.librarysystem.dto.BookWithDetailsDTO;
import com.example.librarysystem.entity.Book;
import com.example.librarysystem.repository.BookRepository;
import com.example.librarysystem.repository.AuthorRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;

    public BookService(BookRepository bookRepository, AuthorRepository authorRepository) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
    }

    public List<BookWithDetailsDTO> getAllBooks() {
        List<Book> books = bookRepository.findAll();
        if (books.isEmpty()) throw new IllegalArgumentException("No books found");
        return books.stream().map(this::toBookDTO).toList();
    }

    public List<BookWithDetailsDTO> searchBooks(String title, Long authorId) {
        if ((title != null && !title.trim().isEmpty()) && authorId != null)
            throw new IllegalArgumentException("Search by either title or authorId, not both");
        List<Book> books = title != null && !title.trim().isEmpty()
                ? bookRepository.findByTitleContainingIgnoreCase(title)
                : authorId != null
                ? bookRepository.findByAuthorId(authorId)
                : null;
        if (books == null || books.isEmpty())
            throw new IllegalArgumentException(books == null ? "Provide title or authorId" : "No books found");
        return books.stream().map(this::toBookDTO).toList();
    }

    public BookWithDetailsDTO createBook(Book book) {
        if (book == null) throw new IllegalArgumentException("Book cannot be null");
        if (book.getTitle() == null || book.getTitle().trim().isEmpty())
            throw new IllegalArgumentException("Title cannot be null or empty");
        if (book.getAuthorId() == null)
            throw new IllegalArgumentException("Author ID cannot be null");
        if (book.getTotalCopies() == null || book.getTotalCopies() < 0)
            throw new IllegalArgumentException("Total copies must be non-negative");
        if (book.getAvailableCopies() == null || book.getAvailableCopies() < 0)
            throw new IllegalArgumentException("Available copies must be non-negative");
        if (book.getAvailableCopies() > book.getTotalCopies())
            throw new IllegalArgumentException("Available copies cannot exceed total copies");
        return toBookDTO(bookRepository.save(book));
    }

    private BookWithDetailsDTO toBookDTO(Book book) {
        String authorName = authorRepository.findById(book.getAuthorId())
                .map(a -> (a.getFirstName() != null ? a.getFirstName() + " " : "") + a.getLastName())
                .orElse("Unknown Author");
        return new BookWithDetailsDTO(
                book.getBookId(),
                book.getTitle(),
                book.getPublicationYear() != null ? book.getPublicationYear() : 0,
                book.getAvailableCopies(),
                book.getTotalCopies(),
                authorName
        );
    }
}