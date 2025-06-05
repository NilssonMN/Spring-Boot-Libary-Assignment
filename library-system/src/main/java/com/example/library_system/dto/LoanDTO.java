package com.example.library_system.dto;

import jakarta.validation.constraints.NotNull;

public class LoanDTO {
    private Long id;
    @NotNull(message = "User ID cannot be null")
    private Long userId;
    @NotNull(message = "Book ID cannot be null")
    private Long bookId;
    private String borrowedDate;
    private String dueDate;
    private String returnedDate;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getBookId() { return bookId; }
    public void setBookId(Long bookId) { this.bookId = bookId; }

    public String getBorrowedDate() { return borrowedDate; }
    public void setBorrowedDate(String borrowedDate) { this.borrowedDate = borrowedDate; }

    public String getDueDate() { return dueDate; }
    public void setDueDate(String dueDate) { this.dueDate = dueDate; }

    public String getReturnedDate() { return returnedDate; }
    public void setReturnedDate(String returnedDate) { this.returnedDate = returnedDate; }
}