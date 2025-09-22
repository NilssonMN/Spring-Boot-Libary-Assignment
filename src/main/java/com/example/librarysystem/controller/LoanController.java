package com.example.librarysystem.controller;

import com.example.librarysystem.entity.Loan;
import com.example.librarysystem.service.LoanService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping
public class LoanController {

    private final LoanService loanService;

    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    @GetMapping("/users/{userId}/loans")
    public List<Loan> getLoansByUserId(@PathVariable Long userId) {
        return loanService.getLoansByUserId(userId);
    }

    @PostMapping("/loans")
    public Loan createLoan(@RequestParam Long userId, @RequestParam Long bookId) {
        return loanService.createLoan(userId, bookId);
    }

    @PutMapping("/loans/{id}/return")
    public Loan returnBook(@PathVariable Long id) {
        return loanService.returnBook(id);
    }

    @PutMapping("/loans/{id}/extend")
    public Loan extendLoan(@PathVariable Long id) {
        return loanService.extendLoan(id);
    }

}