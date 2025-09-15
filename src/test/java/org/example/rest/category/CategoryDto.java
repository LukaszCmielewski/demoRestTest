package org.example.rest.category;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;


@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryDto {

    private Long id;
    private String name;
    private CategoryDto parentCategory;

    public CategoryDto(String name, CategoryDto parentCategory) {
        this.name = name;
        this.parentCategory = parentCategory;
    }
}