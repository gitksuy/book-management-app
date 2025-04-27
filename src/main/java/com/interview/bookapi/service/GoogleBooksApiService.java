package com.interview.bookapi.service;

import com.interview.bookapi.dto.GoogleBookResponse;
import reactor.core.publisher.Mono;

public interface GoogleBooksApiService {
    Mono<GoogleBookResponse> searchBooksByIsbn(String isbn);
    Mono<GoogleBookResponse> searchBooksByTitle(String title);
}