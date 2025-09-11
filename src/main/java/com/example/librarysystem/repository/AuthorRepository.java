package com.example.librarysystem.repository;

import com.example.librarysystem.entity.Author;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuthorRepository extends JpaRepository<Author, Long> {

    List<Author> findByLastNameIgnoreCase(String lastName);
}