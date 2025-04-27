package com.interview.bookapi.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GoogleBookResponse {
    private Integer totalItems;
    private List<Item> items;
    
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Item {
        private String id;
        private VolumeInfo volumeInfo;
        private SaleInfo saleInfo;
        private AccessInfo accessInfo;
        private SearchInfo searchInfo;
    }
    
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class VolumeInfo {
        private String title;
        private List<String> authors;
        private String publisher;
        private String publishedDate;
        private String description;
        private List<IndustryIdentifier> industryIdentifiers;
        private Integer pageCount;
        private List<String> categories;
        private Double averageRating;
        private ImageLinks imageLinks;
        private String language;
        private String previewLink;
        private String infoLink;
    }
    
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class IndustryIdentifier {
        private String type;
        private String identifier;
    }
    
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ImageLinks {
        private String smallThumbnail;
        private String thumbnail;
    }
    
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SaleInfo {
        private String country;
        private String saleability;
        private Boolean isEbook;
    }
    
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AccessInfo {
        private String country;
        private String viewability;
        private Boolean embeddable;
        private Boolean publicDomain;
        private String textToSpeechPermission;
    }
    
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SearchInfo {
        private String textSnippet;
    }
}