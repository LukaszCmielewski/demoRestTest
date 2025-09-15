package org.example.rest.category;

import com.github.javafaker.Faker;
import java.util.UUID;

public class CategoryFactory {
    private static final Faker faker = new Faker();

    public static CategoryDto create(String name, CategoryDto parent) {
        return new CategoryDto(name, parent);
    }

    public static CategoryDto createWithRandomName(CategoryDto parent) {
        String randomName = faker.book().genre() +"_"+ UUID.randomUUID();
        return new CategoryDto(randomName, parent);
    }

    public static CategoryDto createWithoutName() {
        return new CategoryDto("", null);
    }

    public static CategoryDto createWithInvalidParent() {
        CategoryDto invalidParent = new CategoryDto();
        invalidParent.setId(999999L);
        return new CategoryDto("Invalid Parent", invalidParent);
    }
}