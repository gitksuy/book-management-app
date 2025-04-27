package com.interview.bookapi.service;

import com.interview.bookapi.dto.GoogleBookResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class GoogleBooksApiServiceImpl implements GoogleBooksApiService {

    private static final Logger logger = LoggerFactory.getLogger(GoogleBooksApiServiceImpl.class);
    private final WebClient webClient;
    private final String apiUrl;
    private final String apiKey;

    public GoogleBooksApiServiceImpl(
            WebClient.Builder webClientBuilder,
            @Value("${google.books.api.url}") String apiUrl,
            @Value("${google.books.api.key}") String apiKey) {
        this.webClient = webClientBuilder.build();
        this.apiUrl = apiUrl;
        this.apiKey = apiKey;
    }

    @Override
    public Mono<GoogleBookResponse> searchBooksByIsbn(String isbn) {
        logger.debug("Searching Google Books API for ISBN: {}", isbn);
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .host("www.googleapis.com")
                        .path("/books/v1/volumes")
                        .queryParam("q", "isbn:" + isbn)
                        .queryParam("key", apiKey)
                        .build())
                .retrieve()
                .bodyToMono(GoogleBookResponse.class)
                .doOnSuccess(response -> logger.debug("Received response from Google Books API for ISBN: {}", isbn))
                .doOnError(error -> logger.error("Error fetching from Google Books API for ISBN: {}", isbn, error));
    }

    @Override
    public Mono<GoogleBookResponse> searchBooksByTitle(String title) {
        logger.debug("Searching Google Books API for title: {}", title);
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .host("www.googleapis.com")
                        .path("/books/v1/volumes")
                        .queryParam("q", "intitle:" + title)
                        .queryParam("key", apiKey)
                        .build())
                .retrieve()
                .bodyToMono(GoogleBookResponse.class)
                .doOnSuccess(response -> logger.debug("Received response from Google Books API for title: {}", title))
                .doOnError(error -> logger.error("Error fetching from Google Books API for title: {}", title, error));
    }
}