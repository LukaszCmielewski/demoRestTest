package org.example.rest.category;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;


@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryDTO {

    private Long id;
    private String name;
    private CategoryDTO parentCategory;

    public CategoryDTO(String name, CategoryDTO parentCategory) {
        this.name = name;
        this.parentCategory = parentCategory;
    }

    @Override
    public String toString() {

        ObjectMapper mapper = new ObjectMapper();
        String json = null;
        try {
            json = mapper.writeValueAsString(this);
            return json;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}