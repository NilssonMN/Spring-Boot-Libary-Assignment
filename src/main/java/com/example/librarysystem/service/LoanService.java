package com.example.librarysystem.service;

import com.example.librarysystem.entity.Book;
import com.example.librarysystem.entity.Loan;
import com.example.librarysystem.entity.User;
import com.example.librarysystem.repository.BookRepository;
import com.example.librarysystem.repository.LoanRepository;
import com.example.librarysystem.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class LoanService {

    private final LoanRepository loanRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    public LoanService(LoanRepository loanRepository, UserRepository userRepository, BookRepository bookRepository) {
        this.loanRepository = loanRepository;
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
    }

    public List<Loan> getLoansByUserId(Long userId) {
        if (userId == null) throw new IllegalArgumentException("User ID cannot be null");
        List<Loan> loans = loanRepository.findByUserUserId(userId);
        if (loans.isEmpty()) throw new IllegalArgumentException("No loans found for user ID: " + userId);
        return loans;
    }

    public Loan createLoan(Long userId, Long bookId) {
        if (userId == null) throw new IllegalArgumentException("User ID cannot be null");
        if (bookId == null) throw new IllegalArgumentException("Book ID cannot be null");
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Book not found: " + bookId));
        if (book.getAvailableCopies() <= 0) throw new IllegalArgumentException("No copies available for: " + book.getTitle());
        book.setAvailableCopies(book.getAvailableCopies() - 1);
        bookRepository.save(book);
        Loan loan = new Loan();
        loan.setUser(user);
        loan.setBook(book);
        LocalDateTime now = LocalDateTime.now();
        loan.setBorrowedDate(now);
        loan.setDueDate(now.plusDays(14));
        return loanRepository.save(loan);
    }

    public Loan returnBook(Long loanId) {
        if (loanId == null) throw new IllegalArgumentException("Loan ID cannot be null");
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new IllegalArgumentException("Loan not found: " + loanId));
        if (loan.getReturnedDate() != null) throw new IllegalArgumentException("Loan already returned: ID " + loanId);
        loan.setReturnedDate(LocalDateTime.now());
        Book book = loan.getBook();
        book.setAvailableCopies(book.getAvailableCopies() + 1);
        bookRepository.save(book);
        return loanRepository.save(loan);
    }

    public Loan extendLoan(Long loanId) {
        if (loanId == null) throw new IllegalArgumentException("Loan ID cannot be null");
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new IllegalArgumentException("Loan not found: " + loanId));
        if (loan.getReturnedDate() != null) throw new IllegalArgumentException("Cannot extend returned loan: ID " + loanId);
        LocalDateTime newDueDate = loan.getDueDate().plusDays(14);
        loan.setDueDate(newDueDate);
        return loanRepository.save(loan);
    }

}