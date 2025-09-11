package com.example.librarysystem;

import com.example.librarysystem.entity.Book;
import com.example.librarysystem.entity.User;
import com.example.librarysystem.entity.Loan;
import com.example.librarysystem.repository.BookRepository;
import com.example.librarysystem.repository.UserRepository;
import com.example.librarysystem.repository.LoanRepository;
import com.example.librarysystem.service.LoanService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoanServiceMockTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private LoanRepository loanRepository;

    @InjectMocks
    private LoanService loanService;

    @Test
    void createLoan_WhenBookNotAvailable_ShouldThrowException() {
        // ARRANGE
        Book unavailableBook = new Book();
        unavailableBook.setBookId(1L);
        unavailableBook.setTitle("Unavailable Book");
        unavailableBook.setAvailableCopies(0); // No copies available
        User user = new User();
        user.setUserId(1L);

        when(bookRepository.findById(1L)).thenReturn(Optional.of(unavailableBook));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // ACT & ASSERT
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            loanService.createLoan(1L, 1L);
        });
        assertEquals("No copies available for: Unavailable Book", exception.getMessage());
        verify(bookRepository, never()).save(any(Book.class));
        verify(loanRepository, never()).save(any(Loan.class));
    }

    @Test
    void createLoan_WhenBookAvailable_ShouldSetCorrectDueDate() {
        // ARRANGE
        Book availableBook = new Book();
        availableBook.setBookId(1L);
        availableBook.setTitle("Available Book");
        availableBook.setAvailableCopies(3); // Has copies
        User user = new User();
        user.setUserId(1L);
        user.setFirstName("Test");
        user.setLastName("User");

        when(bookRepository.findById(1L)).thenReturn(Optional.of(availableBook));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookRepository.save(any(Book.class))).thenReturn(availableBook);
        when(loanRepository.save(any(Loan.class))).thenAnswer(invocation -> {
            Loan savedLoan = invocation.getArgument(0);
            savedLoan.setLoanId(1L); // Simulate DB ID assignment
            return savedLoan;
        });

        // ACT
        Loan createdLoan = loanService.createLoan(1L, 1L);

        // ASSERT
        assertNotNull(createdLoan, "Created loan should not be null");
        assertEquals(1L, createdLoan.getLoanId());
        assertEquals(user, createdLoan.getUser());
        assertEquals(availableBook, createdLoan.getBook());
        assertNotNull(createdLoan.getBorrowedDate(), "BorrowedDate should not be null");
        assertNotNull(createdLoan.getDueDate(), "DueDate should not be null");
        assertEquals(createdLoan.getBorrowedDate().plusDays(14), createdLoan.getDueDate(), "DueDate should be 14 days from BorrowedDate");
        assertEquals(2, availableBook.getAvailableCopies(), "Available copies should decrease by 1");
        verify(bookRepository).save(availableBook);
        verify(loanRepository).save(any(Loan.class));
    }
}