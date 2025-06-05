package com.example.library_system.repository;

import com.example.library_system.entity.Loan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LoanRepository extends JpaRepository<Loan, Long> {
    List<Loan> findByUserId(Long userId);
    Optional<Loan> findByUserIdAndBookIdAndReturnedDateIsNull(Long userId, Long bookId);
}