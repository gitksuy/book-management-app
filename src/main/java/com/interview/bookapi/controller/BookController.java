package com.interview.bookapi.controller;

import com.interview.bookapi.dto.BookDTO;
import com.interview.bookapi.dto.BookDetailDTO;
import com.interview.bookapi.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/books")
@Tag(name = "Book Controller", description = "APIs for managing books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @PostMapping
    @Operation(summary = "Create a new book", description = "Creates a new book in the database")
    @ApiResponse(responseCode = "201", description = "Book created successfully")
    public ResponseEntity<BookDTO> createBook(@Valid @RequestBody BookDTO bookDTO) {
        BookDTO createdBook = bookService.createBook(bookDTO);
        return new ResponseEntity<>(createdBook, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a book", description = "Updates an existing book by ID")
    @ApiResponse(responseCode = "200", description = "Book updated successfully")
    public ResponseEntity<BookDTO> updateBook(@PathVariable Long id, @Valid @RequestBody BookDTO bookDTO) {
        BookDTO updatedBook = bookService.updateBook(id, bookDTO);
        return ResponseEntity.ok(updatedBook);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a book", description = "Deletes a book by ID")
    @ApiResponse(responseCode = "204", description = "Book deleted successfully")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a book by ID", description = "Retrieves a book by its ID")
    @ApiResponse(responseCode = "200", description = "Book retrieved successfully")
    public ResponseEntity<BookDTO> getBookById(@PathVariable Long id) {
        BookDTO book = bookService.getBookById(id);
        return ResponseEntity.ok(book);
    }

    @GetMapping
    @Operation(summary = "Get all books with pagination", description = "Retrieves all books with pagination (10 per page)")
    @ApiResponse(responseCode = "200", description = "Books retrieved successfully")
    public ResponseEntity<Page<BookDTO>> getAllBooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<BookDTO> books = bookService.getAllBooks(page, size);
        return ResponseEntity.ok(books);
    }

    @GetMapping("/search")
    @Operation(summary = "Search books", description = "Search books by title, author, or genre")
    @ApiResponse(responseCode = "200", description = "Search results retrieved successfully")
    public ResponseEntity<Page<BookDTO>> searchBooks(
            @RequestParam String query,
            @RequestParam(defaultValue = "title") String searchBy,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<BookDTO> books = bookService.searchBooks(query, searchBy, page, size);
        return ResponseEntity.ok(books);
    }

    @GetMapping("/{id}/details")
    @Operation(summary = "Get book details with Google Books info", description = "Retrieves detailed book information including data from Google Books API")
    @ApiResponse(responseCode = "200", description = "Book details retrieved successfully")
    public ResponseEntity<BookDetailDTO> getBookDetailsWithGoogleBooksInfo(@PathVariable Long id) {
        BookDetailDTO bookDetails = bookService.getBookDetailsWithGoogleBooksInfo(id);
        return ResponseEntity.ok(bookDetails);
    }
}