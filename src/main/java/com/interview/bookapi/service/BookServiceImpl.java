package com.interview.bookapi.service;

import com.interview.bookapi.dto.BookDTO;
import com.interview.bookapi.dto.BookDetailDTO;
import com.interview.bookapi.dto.GoogleBookResponse;
import com.interview.bookapi.entity.Book;
import com.interview.bookapi.exception.BookNotFoundException;
import com.interview.bookapi.repository.BookRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
public class BookServiceImpl implements BookService {

    private static final Logger logger = LoggerFactory.getLogger(BookServiceImpl.class);
    private final BookRepository bookRepository;
    private final GoogleBooksApiService googleBooksApiService;

    public BookServiceImpl(BookRepository bookRepository, GoogleBooksApiService googleBooksApiService) {
        this.bookRepository = bookRepository;
        this.googleBooksApiService = googleBooksApiService;
    }

    @Override
    @Transactional
    public BookDTO createBook(BookDTO bookDTO) {
        logger.info("Creating new book: {}", bookDTO.getTitle());
        Book book = new Book();
        BeanUtils.copyProperties(bookDTO, book);
        book = bookRepository.save(book);
        BookDTO savedBookDTO = new BookDTO();
        BeanUtils.copyProperties(book, savedBookDTO);
        logger.info("Book created successfully with ID: {}", savedBookDTO.getId());
        return savedBookDTO;
    }

    @Override
    @Transactional
    public BookDTO updateBook(Long id, BookDTO bookDTO) {
        logger.info("Updating book with ID: {}", id);
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException("Book not found with ID: " + id));
        
        BeanUtils.copyProperties(bookDTO, book, "id", "createdAt");
        book = bookRepository.save(book);
        
        BookDTO updatedBookDTO = new BookDTO();
        BeanUtils.copyProperties(book, updatedBookDTO);
        logger.info("Book updated successfully with ID: {}", updatedBookDTO.getId());
        return updatedBookDTO;
    }

    @Override
    @Transactional
    public void deleteBook(Long id) {
        logger.info("Deleting book with ID: {}", id);
        if (!bookRepository.existsById(id)) {
            throw new BookNotFoundException("Book not found with ID: " + id);
        }
        bookRepository.deleteById(id);
        logger.info("Book deleted successfully with ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public BookDTO getBookById(Long id) {
        logger.info("Fetching book with ID: {}", id);
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException("Book not found with ID: " + id));
        
        BookDTO bookDTO = new BookDTO();
        BeanUtils.copyProperties(book, bookDTO);
        logger.info("Book fetched successfully with ID: {}", bookDTO.getId());
        return bookDTO;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BookDTO> getAllBooks(int page, int size) {
        logger.info("Fetching books page {} with size {}", page, size);
        Page<Book> bookPage = bookRepository.findAll(
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")));
        
        Page<BookDTO> bookDTOPage = bookPage.map(book -> {
            BookDTO bookDTO = new BookDTO();
            BeanUtils.copyProperties(book, bookDTO);
            return bookDTO;
        });
        
        logger.info("Fetched {} books successfully", bookDTOPage.getContent().size());
        return bookDTOPage;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BookDTO> searchBooks(String query, String searchBy, int page, int size) {
        logger.info("Searching books by {} with query: {}, page: {}, size: {}", searchBy, query, page, size);
        Page<Book> bookPage;
        
        switch (searchBy.toLowerCase()) {
            case "title":
                bookPage = bookRepository.findByTitleContainingIgnoreCase(query, 
                        PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "title")));
                break;
            case "author":
                bookPage = bookRepository.findByAuthorContainingIgnoreCase(query, 
                        PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "author")));
                break;
            case "category":
                bookPage = bookRepository.findByCategoryContainingIgnoreCase(query, 
                        PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "category")));
                break;
            default:
                bookPage = bookRepository.findByTitleContainingIgnoreCase(query, 
                        PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "title")));
        }
        
        Page<BookDTO> bookDTOPage = bookPage.map(book -> {
            BookDTO bookDTO = new BookDTO();
            BeanUtils.copyProperties(book, bookDTO);
            return bookDTO;
        });
        
        logger.info("Search returned {} books", bookDTOPage.getContent().size());
        return bookDTOPage;
    }

    @Override
    @Transactional(readOnly = true)
    public BookDetailDTO getBookDetailsWithGoogleBooksInfo(Long id) {
        logger.info("Fetching detailed book information with Google Books data for ID: {}", id);
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException("Book not found with ID: " + id));
        
        BookDetailDTO bookDetailDTO = new BookDetailDTO();
        BeanUtils.copyProperties(book, bookDetailDTO);
        
        // Fetch additional information from Google Books API
        if (book.getIsbn() != null && !book.getIsbn().isEmpty()) {
            GoogleBookResponse response = googleBooksApiService.searchBooksByIsbn(book.getIsbn()).block();
            
            if (response != null && response.getItems() != null && !response.getItems().isEmpty()) {
                GoogleBookResponse.Item item = response.getItems().get(0);
                
                // Update with Google Books information
                if (item.getVolumeInfo() != null) {
                    bookDetailDTO.setInfoLink(item.getVolumeInfo().getInfoLink());
                }
                
                if (item.getSearchInfo() != null) {
                    bookDetailDTO.setTextSnippet(item.getSearchInfo().getTextSnippet());
                }
                
                if (item.getSaleInfo() != null) {
                    bookDetailDTO.setIsEbook(item.getSaleInfo().getIsEbook());
                }
                
                logger.info("Enhanced book details with Google Books information");
            } else {
                logger.info("No Google Books information found for ISBN: {}", book.getIsbn());
            }
        } else {
            logger.info("Book has no ISBN, skipping Google Books API call");
        }
        
        return bookDetailDTO;
    }
}