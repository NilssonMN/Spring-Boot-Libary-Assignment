package com.example.library_system.service;

import com.example.library_system.dto.LoanDTO;
import com.example.library_system.entity.Book;
import com.example.library_system.entity.Loan;
import com.example.library_system.entity.User;
import com.example.library_system.repository.BookRepository;
import com.example.library_system.repository.LoanRepository;
import com.example.library_system.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LoanServiceTest {

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private LoanService loanService;

    private Book book;
    private User user;
    private Loan loan;
    private LoanDTO loanDTO;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @BeforeEach
    void setUp() {
        book = new Book();
        book.setId(1L);
        book.setTitle("Test Book");
        book.setAvailableCopies(5);
        book.setTotalCopies(5);
        book.setAuthorId(1L);

        user = new User();
        user.setId(1L);
        user.setFirstName("Jane");
        user.setLastName("Doe");
        user.setEmail("jane.doe@example.com");

        loan = new Loan();
        loan.setId(1L);
        loan.setUserId(1L);
        loan.setBookId(1L);
        loan.setBorrowedDate(LocalDateTime.now().format(formatter));
        loan.setDueDate(LocalDateTime.now().plusDays(14).format(formatter));

        loanDTO = new LoanDTO();
        loanDTO.setUserId(1L);
        loanDTO.setBookId(1L);
    }

    @Test
    void createLoan_setsCorrectDueDate() {
        LocalDateTime now = LocalDateTime.now();
        String expectedDueDate = now.plusDays(14).format(formatter);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(loanRepository.findByUserIdAndBookIdAndReturnedDateIsNull(1L, 1L)).thenReturn(Optional.empty());
        when(loanRepository.save(any(Loan.class))).thenAnswer(invocation -> {
            Loan savedLoan = invocation.getArgument(0);
            savedLoan.setId(1L);
            savedLoan.setBorrowedDate(now.format(formatter));
            savedLoan.setDueDate(expectedDueDate);
            return savedLoan;
        });
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        ResponseEntity<LoanDTO> response = loanService.createLoan(loanDTO);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        LoanDTO result = response.getBody();
        assertNotNull(result);
        assertEquals(expectedDueDate, result.getDueDate());
        verify(userRepository, times(1)).findById(1L);
        verify(bookRepository, times(1)).findById(1L);
        verify(loanRepository, times(1)).findByUserIdAndBookIdAndReturnedDateIsNull(1L, 1L);
        verify(bookRepository, times(1)).save(book);
        verify(loanRepository, times(1)).save(any(Loan.class));
    }

    @Test
    void createLoan_noAvailableCopies_returnsBadRequest() {
        book.setAvailableCopies(0);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(loanRepository.findByUserIdAndBookIdAndReturnedDateIsNull(1L, 1L)).thenReturn(Optional.empty());

        ResponseEntity<LoanDTO> response = loanService.createLoan(loanDTO);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
        verify(userRepository, times(1)).findById(1L);
        verify(bookRepository, times(1)).findById(1L);
        verify(loanRepository, times(1)).findByUserIdAndBookIdAndReturnedDateIsNull(1L, 1L);
        verify(bookRepository, never()).save(any());
        verify(loanRepository, never()).save(any());
    }
}