package com.interview.bookapi.service;

import com.interview.bookapi.dto.BookDTO;
import com.interview.bookapi.dto.BookDetailDTO;
import org.springframework.data.domain.Page;

public interface BookService {
    BookDTO createBook(BookDTO bookDTO);
    BookDTO updateBook(Long id, BookDTO bookDTO);
    void deleteBook(Long id);
    BookDTO getBookById(Long id);
    Page<BookDTO> getAllBooks(int page, int size);
    Page<BookDTO> searchBooks(String query, String searchBy, int page, int size);
    BookDetailDTO getBookDetailsWithGoogleBooksInfo(Long id);
}