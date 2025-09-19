package org.example.rest.book;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.rest.author.AuthorDTO;
import org.example.rest.category.CategoryDTO;
import org.example.rest.publisher.PublisherDTO;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookDTO {
    private Long id;
    private String title;
    private String isbn;
    private int publicationYear;
    private String edition;
    private String summary;
    private String coverImageUrl;
    private String language;
    private PublisherDTO publisher;
    private List<AuthorDTO> authors;
    private List<CategoryDTO> categories;
    private boolean available;

    // Konstruktor
    public BookDTO(String title, String isbn, int publicationYear, String edition, String summary,
                   String coverImageUrl, String language, PublisherDTO publisher, List<AuthorDTO> authors, List<CategoryDTO> categories, boolean available) {
        this.title = title;
        this.isbn = isbn;
        this.publicationYear = publicationYear;
        this.edition = edition;
        this.summary = summary;
        this.coverImageUrl = coverImageUrl;
        this.language = language;
        this.publisher = publisher;
        this.authors = authors;
        this.categories = categories;
        this.available = available;
    }

//    public boolean isValidIsbn() {
//        return isbn != null && isbn.matches("^(\\d{9}[\\dX]|\\d{13})$");
//    }
//
//    // Walidacja roku publikacji
//    public boolean isValidPublicationYear() {
//        return publicationYear > 1500 && publicationYear <= 2100;
//    }

    private static final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule())
            .disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    @Override
    public String toString() {
        try {
            return mapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public String prettyPrint() {
        try {
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(this);
        } catch (Exception e) {
            throw new RuntimeException("Błąd podczas generowania pretty print JSON", e);
        }
    }

}
