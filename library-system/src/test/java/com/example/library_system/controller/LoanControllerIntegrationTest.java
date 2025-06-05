package com.example.library_system.controller;

import com.example.library_system.dto.LoanDTO;
import com.example.library_system.entity.Book;
import com.example.library_system.entity.User;
import com.example.library_system.repository.BookRepository;
import com.example.library_system.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
public class LoanControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Book book;
    private User user;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @BeforeEach
    void setUp() {
        // Clear test database
        bookRepository.deleteAll();
        userRepository.deleteAll();

        // Setup test data
        book = new Book();
        book.setTitle("Test Book");
        book.setPublicationYear(2020);
        book.setAvailableCopies(5);
        book.setTotalCopies(5);
        book.setAuthorId(1L);
        bookRepository.save(book);

        user = new User();
        user.setFirstName("Jane");
        user.setLastName("Doe");
        user.setEmail("jane.doe@example.com");
        user.setPassword("secure123");
        user.setRegistrationDate(LocalDateTime.now().format(formatter));
        userRepository.save(user);
    }

    @Test
    void createLoan_success() throws Exception {
        LoanDTO loanDTO = new LoanDTO();
        loanDTO.setUserId(user.getId());
        loanDTO.setBookId(book.getId());

        mockMvc.perform(post("/loans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loanDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.userId").value(user.getId()))
                .andExpect(jsonPath("$.bookId").value(book.getId()))
                .andExpect(jsonPath("$.borrowedDate").isNotEmpty())
                .andExpect(jsonPath("$.dueDate").isNotEmpty())
                .andExpect(jsonPath("$.returnedDate").doesNotExist());
    }

    @Test
    void createLoan_bookNotFound_returnsBadRequest() throws Exception {
        LoanDTO loanDTO = new LoanDTO();
        loanDTO.setUserId(user.getId());
        loanDTO.setBookId(999L); // Non-existent book ID

        mockMvc.perform(post("/loans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loanDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createLoan_noAvailableCopies_returnsBadRequest() throws Exception {
        book.setAvailableCopies(0);
        bookRepository.save(book);

        LoanDTO loanDTO = new LoanDTO();
        loanDTO.setUserId(user.getId());
        loanDTO.setBookId(book.getId());

        mockMvc.perform(post("/loans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loanDTO)))
                .andExpect(status().isBadRequest());
    }
}