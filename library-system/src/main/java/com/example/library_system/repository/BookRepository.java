package com.example.library_system.repository;

import com.example.library_system.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {
    @Query("SELECT b FROM Book b WHERE " +
            "(:title IS NULL OR b.title LIKE %:title%) AND " +
            "(:authorId IS NULL OR b.authorId = :authorId)")
    List<Book> searchBooks(@Param("title") String title, @Param("authorId") Long authorId);
}