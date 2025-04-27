package com.interview.bookapi.service;

import com.interview.bookapi.dto.BookDTO;
import com.interview.bookapi.dto.BookDetailDTO;
import com.interview.bookapi.dto.GoogleBookResponse;
import com.interview.bookapi.entity.Book;
import com.interview.bookapi.exception.BookNotFoundException;
import com.interview.bookapi.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private GoogleBooksApiService googleBooksApiService;

    @InjectMocks
    private BookServiceImpl bookService;

    private Book testBook;
    private BookDTO testBookDTO;
    private List<Book> bookList;
    private GoogleBookResponse googleBookResponse;

    @BeforeEach
    void setUp() {
        // Setup test book
        testBook = new Book();
        testBook.setId(1L);
        testBook.setTitle("Test Book");
        testBook.setAuthor("Test Author");
        testBook.setDescription("Test Description");
        testBook.setIsbn("1234567890");
        testBook.setCategory("Test Category");
        testBook.setPageCount(200);
        testBook.setPublisher("Test Publisher");
        testBook.setPublishedDate("2023-01-01");
        testBook.setCreatedAt(LocalDateTime.now());
        testBook.setUpdatedAt(LocalDateTime.now());

        // Setup test BookDTO
        testBookDTO = new BookDTO();
        BeanUtils.copyProperties(testBook, testBookDTO);

        // Setup book list
        Book book2 = new Book();
        book2.setId(2L);
        book2.setTitle("Another Book");
        book2.setAuthor("Another Author");
        book2.setIsbn("0987654321");
        book2.setCreatedAt(LocalDateTime.now());
        
        bookList = Arrays.asList(testBook, book2);

        // Setup Google Book Response
        googleBookResponse = new GoogleBookResponse();
        GoogleBookResponse.Item item = new GoogleBookResponse.Item();
        
        GoogleBookResponse.VolumeInfo volumeInfo = new GoogleBookResponse.VolumeInfo();
        volumeInfo.setInfoLink("http://test.info.link");
        item.setVolumeInfo(volumeInfo);
        
        GoogleBookResponse.SaleInfo saleInfo = new GoogleBookResponse.SaleInfo();
        saleInfo.setIsEbook(true);
        item.setSaleInfo(saleInfo);
        
        GoogleBookResponse.SearchInfo searchInfo = new GoogleBookResponse.SearchInfo();
        searchInfo.setTextSnippet("Test snippet");
        item.setSearchInfo(searchInfo);
        
        googleBookResponse.setItems(Arrays.asList(item));
    }

    @Test
    void createBook_ShouldReturnSavedBookDTO() {
        // Given
        when(bookRepository.save(any(Book.class))).thenReturn(testBook);

        // When
        BookDTO result = bookService.createBook(testBookDTO);

        // Then
        assertNotNull(result);
        assertEquals(testBook.getId(), result.getId());
        assertEquals(testBook.getTitle(), result.getTitle());
        assertEquals(testBook.getAuthor(), result.getAuthor());
        verify(bookRepository, times(1)).save(any(Book.class));
    }

    @Test
    void updateBook_WhenBookExists_ShouldReturnUpdatedBookDTO() {
        // Given
        Long bookId = 1L;
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(testBook));
        when(bookRepository.save(any(Book.class))).thenReturn(testBook);

        BookDTO updatedBookDTO = new BookDTO();
        updatedBookDTO.setTitle("Updated Title");
        updatedBookDTO.setAuthor("Updated Author");

        // When
        BookDTO result = bookService.updateBook(bookId, updatedBookDTO);

        // Then
        assertNotNull(result);
        assertEquals(bookId, result.getId());
        verify(bookRepository, times(1)).findById(bookId);
        verify(bookRepository, times(1)).save(any(Book.class));
    }

    @Test
    void updateBook_WhenBookDoesNotExist_ShouldThrowException() {
        // Given
        Long bookId = 999L;
        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(BookNotFoundException.class, () -> {
            bookService.updateBook(bookId, testBookDTO);
        });
        verify(bookRepository, times(1)).findById(bookId);
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    void deleteBook_WhenBookExists_ShouldDeleteSuccessfully() {
        // Given
        Long bookId = 1L;
        when(bookRepository.existsById(bookId)).thenReturn(true);
        doNothing().when(bookRepository).deleteById(bookId);

        // When
        bookService.deleteBook(bookId);

        // Then
        verify(bookRepository, times(1)).existsById(bookId);
        verify(bookRepository, times(1)).deleteById(bookId);
    }

    @Test
    void deleteBook_WhenBookDoesNotExist_ShouldThrowException() {
        // Given
        Long bookId = 999L;
        when(bookRepository.existsById(bookId)).thenReturn(false);

        // When & Then
        assertThrows(BookNotFoundException.class, () -> {
            bookService.deleteBook(bookId);
        });
        verify(bookRepository, times(1)).existsById(bookId);
        verify(bookRepository, never()).deleteById(any());
    }

    @Test
    void getBookById_WhenBookExists_ShouldReturnBookDTO() {
        // Given
        Long bookId = 1L;
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(testBook));

        // When
        BookDTO result = bookService.getBookById(bookId);

        // Then
        assertNotNull(result);
        assertEquals(bookId, result.getId());
        assertEquals(testBook.getTitle(), result.getTitle());
        verify(bookRepository, times(1)).findById(bookId);
    }

    @Test
    void getBookById_WhenBookDoesNotExist_ShouldThrowException() {
        // Given
        Long bookId = 999L;
        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(BookNotFoundException.class, () -> {
            bookService.getBookById(bookId);
        });
        verify(bookRepository, times(1)).findById(bookId);
    }

    @Test
    void getAllBooks_ShouldReturnPageOfBookDTOs() {
        // Given
        int page = 0;
        int size = 10;
        Page<Book> bookPage = new PageImpl<>(bookList);
        when(bookRepository.findAll(any(PageRequest.class))).thenReturn(bookPage);

        // When
        Page<BookDTO> result = bookService.getAllBooks(page, size);

        // Then
        assertNotNull(result);
        assertEquals(bookList.size(), result.getTotalElements());
        verify(bookRepository, times(1)).findAll(any(PageRequest.class));
    }

    @Test
    void searchBooks_ByTitle_ShouldReturnMatchingBooks() {
        // Given
        String query = "test";
        String searchBy = "title";
        int page = 0;
        int size = 10;
        Page<Book> bookPage = new PageImpl<>(Arrays.asList(testBook));
        when(bookRepository.findByTitleContainingIgnoreCase(eq(query), any(Pageable.class)))
                .thenReturn(bookPage);

        // When
        Page<BookDTO> result = bookService.searchBooks(query, searchBy, page, size);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(bookRepository, times(1))
                .findByTitleContainingIgnoreCase(eq(query), any(Pageable.class));
    }

    @Test
    void searchBooks_ByAuthor_ShouldReturnMatchingBooks() {
        // Given
        String query = "test";
        String searchBy = "author";
        int page = 0;
        int size = 10;
        Page<Book> bookPage = new PageImpl<>(Arrays.asList(testBook));
        when(bookRepository.findByAuthorContainingIgnoreCase(eq(query), any(Pageable.class)))
                .thenReturn(bookPage);

        // When
        Page<BookDTO> result = bookService.searchBooks(query, searchBy, page, size);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(bookRepository, times(1))
                .findByAuthorContainingIgnoreCase(eq(query), any(Pageable.class));
    }

    @Test
    void searchBooks_ByCategory_ShouldReturnMatchingBooks() {
        // Given
        String query = "test";
        String searchBy = "category";
        int page = 0;
        int size = 10;
        Page<Book> bookPage = new PageImpl<>(Arrays.asList(testBook));
        when(bookRepository.findByCategoryContainingIgnoreCase(eq(query), any(Pageable.class)))
                .thenReturn(bookPage);

        // When
        Page<BookDTO> result = bookService.searchBooks(query, searchBy, page, size);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(bookRepository, times(1))
                .findByCategoryContainingIgnoreCase(eq(query), any(Pageable.class));
    }

    @Test
    void searchBooks_WithInvalidSearchBy_ShouldDefaultToTitle() {
        // Given
        String query = "test";
        String searchBy = "invalid";
        int page = 0;
        int size = 10;
        Page<Book> bookPage = new PageImpl<>(Arrays.asList(testBook));
        when(bookRepository.findByTitleContainingIgnoreCase(eq(query), any(Pageable.class)))
                .thenReturn(bookPage);

        // When
        Page<BookDTO> result = bookService.searchBooks(query, searchBy, page, size);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(bookRepository, times(1))
                .findByTitleContainingIgnoreCase(eq(query), any(Pageable.class));
    }

    @Test
    void getBookDetailsWithGoogleBooksInfo_ShouldReturnEnhancedBookDetails() {
        // Given
        Long bookId = 1L;
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(testBook));
        when(googleBooksApiService.searchBooksByIsbn(testBook.getIsbn())).thenReturn(Mono.just(googleBookResponse));

        // When
        BookDetailDTO result = bookService.getBookDetailsWithGoogleBooksInfo(bookId);

        // Then
        assertNotNull(result);
        assertEquals(bookId, result.getId());
        assertEquals(testBook.getTitle(), result.getTitle());
        assertEquals("http://test.info.link", result.getInfoLink());
        assertEquals("Test snippet", result.getTextSnippet());
        assertTrue(result.getIsEbook());
        verify(bookRepository, times(1)).findById(bookId);
        verify(googleBooksApiService, times(1)).searchBooksByIsbn(testBook.getIsbn());
    }

    @Test
    void getBookDetailsWithGoogleBooksInfo_WhenNoGoogleData_ShouldReturnBasicDetails() {
        // Given
        Long bookId = 1L;
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(testBook));
        when(googleBooksApiService.searchBooksByIsbn(testBook.getIsbn())).thenReturn(Mono.just(new GoogleBookResponse()));

        // When
        BookDetailDTO result = bookService.getBookDetailsWithGoogleBooksInfo(bookId);

        // Then
        assertNotNull(result);
        assertEquals(bookId, result.getId());
        assertEquals(testBook.getTitle(), result.getTitle());
        assertNull(result.getInfoLink());
        assertNull(result.getTextSnippet());
        assertNull(result.getIsEbook());
        verify(bookRepository, times(1)).findById(bookId);
        verify(googleBooksApiService, times(1)).searchBooksByIsbn(testBook.getIsbn());
    }

    @Test
    void getBookDetailsWithGoogleBooksInfo_WithNoIsbn_ShouldSkipGoogleApiCall() {
        // Given
        Long bookId = 1L;
        testBook.setIsbn(null);
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(testBook));

        // When
        BookDetailDTO result = bookService.getBookDetailsWithGoogleBooksInfo(bookId);

        // Then
        assertNotNull(result);
        assertEquals(bookId, result.getId());
        assertEquals(testBook.getTitle(), result.getTitle());
        verify(bookRepository, times(1)).findById(bookId);
        verify(googleBooksApiService, never()).searchBooksByIsbn(anyString());
    }
}