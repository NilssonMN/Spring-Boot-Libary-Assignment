# Spring Boot Libary Assignment

A school project built in Java, featuring a simple library REST API. Using Spring Boot, Spring Web, Spring Data JPA, SQLite and Maven
---

## Features

### Books
- List all books  
- Search books by title or author ID  
- Create a new book  
- View detailed book information including author details  

### Authors
- List all authors  
- Retrieve authors by last name  
- Create a new author  

### Users
- Retrieve user by email  
- Create a new user  
- View a user’s loan history  

### Loans
- View a user’s loans  
- Create a new loan  
- Return a book  
- Extend a loan by 14 days 

---

## Service Logic
- Checks book availability before creating a loan  
- Updates available book copies when loans are created or returned  
- Sets loan due date to 14 days from borrowing date  
- Prevents loan creation if the user has an active loan for the same book  
- Prevents extending overdue loans  

---

## Testing
- **Unit Tests**  
  - `LoanService.createLoan`: verifies due date assignment and handles no available copies scenario  
- **Integration Tests**  
  - `POST /loans`: tests success cases and edge cases such as no available copies 
