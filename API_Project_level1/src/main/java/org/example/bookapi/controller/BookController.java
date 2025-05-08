package org.example.bookapi.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.bookapi.dto.BookRequest;
import org.example.bookapi.dto.BookResponse;
import org.example.bookapi.service.BookService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/books")
@RequiredArgsConstructor
public class BookController {
    private final BookService bookService;

    @PostMapping
    public ResponseEntity<BookResponse> createBook(@RequestBody @Valid BookRequest request){
        BookResponse response = bookService.createBook(request);
        return ResponseEntity.ok(response);
    }


    @DeleteMapping("/{id}")
    private ResponseEntity<Void> deleteBook(@PathVariable Long id){
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<Page<BookResponse>> getAllBooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<BookResponse> bookResponses = bookService.getAllBooks(pageable);
        return ResponseEntity.ok(bookResponses);
    }


    @PutMapping("/{id}")
    public ResponseEntity<BookResponse> updateBook(@PathVariable Long id, @RequestBody @Valid BookRequest request) {
        BookResponse bookResponse = bookService.updateBook(id, request);
        return ResponseEntity.ok(bookResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookResponse> getBook(@PathVariable Long id) {
        BookResponse bookResponse = bookService.getBook(id);
        return ResponseEntity.ok(bookResponse);
    }

    @PostMapping("/search")
    public ResponseEntity<Page<BookResponse>> searchBooks(@RequestBody BookRequest searchRequest,
                                                          @RequestParam(defaultValue = "0") int page,
                                                          @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<BookResponse> bookResponses = bookService.searchBooks(searchRequest.getTitle(), searchRequest.getAuthor(), pageable);
        return ResponseEntity.ok(bookResponses);
    }

}
