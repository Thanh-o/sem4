package org.example.bookapi.service;

import org.example.bookapi.dto.BookRequest;
import org.example.bookapi.dto.BookResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BookService {
    BookResponse createBook(BookRequest request);
    BookResponse updateBook(Long id,BookRequest request);
    BookResponse getBook(Long id);
    Page<BookResponse> getAllBooks(Pageable pageable);
    void deleteBook(Long id);
    Page<BookResponse> searchBooks(String title, String author, Pageable pageable);

}
