package com.example.library_system.service;

import com.example.library_system.dto.LoanDTO;
import com.example.library_system.entity.Book;
import com.example.library_system.entity.Loan;
import com.example.library_system.entity.User;
import com.example.library_system.repository.BookRepository;
import com.example.library_system.repository.LoanRepository;
import com.example.library_system.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
@Validated
public class LoanService {

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    public ResponseEntity<LoanDTO> createLoan(LoanDTO loanDTO) {
        try {
            // Validate input
            if (loanDTO.getUserId() == null || loanDTO.getUserId() <= 0) {
                throw new IllegalArgumentException("User ID must be positive");
            }
            if (loanDTO.getBookId() == null || loanDTO.getBookId() <= 0) {
                throw new IllegalArgumentException("Book ID must be positive");
            }
            // Validate user exists
            User user = userRepository.findById(loanDTO.getUserId())
                    .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + loanDTO.getUserId()));
            // Validate book exists
            Book book = bookRepository.findById(loanDTO.getBookId())
                    .orElseThrow(() -> new IllegalArgumentException("Book not found with ID: " + loanDTO.getBookId()));
            // Check if user already has an active loan for this book
            if (loanRepository.findByUserIdAndBookIdAndReturnedDateIsNull(loanDTO.getUserId(), loanDTO.getBookId()).isPresent()) {
                throw new IllegalStateException("User already has an active loan for this book");
            }
            // Validate book availability
            if (book.getAvailableCopies() <= 0) {
                throw new IllegalStateException("No available copies of the book");
            }
            Loan loan = new Loan();
            loan.setUserId(loanDTO.getUserId());
            loan.setBookId(loanDTO.getBookId());
            loan.setBorrowedDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            loan.setDueDate(LocalDateTime.now().plusDays(14).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            book.setAvailableCopies(book.getAvailableCopies() - 1);
            bookRepository.save(book);
            Loan savedLoan = loanRepository.save(loan);
            return ResponseEntity.ok(convertToDTO(savedLoan));
        } catch (IllegalStateException | IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @Transactional
    public ResponseEntity<LoanDTO> returnBook(Long id) {
        if (id == null || id <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        Optional<Loan> loanOptional = loanRepository.findById(id);
        if (loanOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        Loan loan = loanOptional.get();
        if (loan.getReturnedDate() != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        if (loan.getBookId() == null) {
            throw new IllegalStateException("Loan with ID " + loan.getId() + " has no associated book ID.");
        }
        Book book = bookRepository.findById(loan.getBookId())
                .orElseThrow(() -> new IllegalArgumentException("Book not found with ID: " + loan.getBookId()));
        book.setAvailableCopies(book.getAvailableCopies() + 1);
        bookRepository.save(book);
        loan.setReturnedDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        Loan savedLoan = loanRepository.save(loan);
        return ResponseEntity.ok(convertToDTO(savedLoan));
    }

    @Transactional
    public ResponseEntity<LoanDTO> extendLoan(Long id) {
        if (id == null || id <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        Optional<Loan> loanOptional = loanRepository.findById(id);
        if (loanOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        Loan loan = loanOptional.get();
        if (loan.getReturnedDate() != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        if (loan.getDueDate() == null || loan.getDueDate().trim().isEmpty()) {
            throw new IllegalStateException("Loan with ID " + loan.getId() + " has a missing or invalid due date.");
        }
        LocalDateTime dueDate = LocalDateTime.parse(loan.getDueDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        if (dueDate.isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Cannot extend overdue loan");
        }
        dueDate = dueDate.plusDays(14);
        loan.setDueDate(dueDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        Loan savedLoan = loanRepository.save(loan);
        return ResponseEntity.ok(convertToDTO(savedLoan));
    }

    private LoanDTO convertToDTO(Loan loan) {
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