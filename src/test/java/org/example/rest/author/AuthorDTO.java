package org.example.rest.author;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthorDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String biography;
    AuthorDTO(String firstName, String lastName, String biography){
        this.firstName=firstName;

    }
    public String prettyPrint() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(this);
        } catch (Exception e) {
            throw new RuntimeException("Błąd podczas generowania pretty print JSON", e);
        }
    }
}
