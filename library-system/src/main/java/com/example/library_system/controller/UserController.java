package com.example.library_system.controller;
import com.example.library_system.dto.LoanDTO;
import com.example.library_system.dto.UserDTO;
import com.example.library_system.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    //Hämta användare via email - http://localhost:8080/users/email/anna.andersson@email.com
    @GetMapping("/email/{email}")
    public ResponseEntity<UserDTO> getUserByEmail(@PathVariable String email) {
        return userService.getUserByEmail(email);
    }


    /*Skapa ny användare -POST http://localhost:8080/users
    {
    "firstName": "Newtestexample",
    "lastName": "testing",
    "email": "test.testing@example.com"
    }
     */
    @PostMapping
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody UserDTO userDTO) {
        return userService.createUser(userDTO);
    }

    //Hämta användarens lån - http://localhost:8080/users/1/loans
    @GetMapping("/{userId}/loans")
    public ResponseEntity<List<LoanDTO>> getUserLoans(@PathVariable Long userId) {
        return userService.getUserLoans(userId);
    }


}