package com.example.librarysystem.repository;

import com.example.librarysystem.entity.Loan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LoanRepository extends JpaRepository<Loan, Long> {
    List<Loan> findByUserUserId(Long userId);
}