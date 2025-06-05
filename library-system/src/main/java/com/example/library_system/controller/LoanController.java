package com.example.library_system.controller;

import com.example.library_system.dto.LoanDTO;
import com.example.library_system.service.LoanService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/loans")
public class LoanController {

    @Autowired
    private LoanService loanService;

    /*Låna bok - POST http://localhost:8080/loans
    {
    "userId": 1,
    "bookId": 27,
    }
     */
    @PostMapping
    public ResponseEntity<LoanDTO> createLoan(@Valid @RequestBody LoanDTO loanDTO) {
        return loanService.createLoan(loanDTO);
    }


    /*Retunera bok - PUT http://localhost:8080/loans/1/return
    {
    "id": välj ett id tex 50,
    "userId": 1,
    "bookId": 1,
    }
     */
    @PutMapping("/{id}/return")
    public ResponseEntity<LoanDTO> returnBook(@PathVariable Long id) {
        return loanService.returnBook(id);
    }


    // Förläng lån - PUT http://localhost:8080/loans/1/extend

    @PutMapping("/{id}/extend")
    public ResponseEntity<LoanDTO> extendLoan(@PathVariable Long id) {
        return loanService.extendLoan(id);
    }
}