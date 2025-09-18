package org.example.rest.category;

import com.github.javafaker.Faker;
import java.util.UUID;

public class CategoryFactory {
    private static final Faker faker = new Faker();

    public static CategoryDTO create(String name, CategoryDTO parent) {
        return new CategoryDTO(name, parent);
    }

    public static CategoryDTO createWithRandomName(CategoryDTO parent) {
        String randomName = faker.book().genre() +"_"+ UUID.randomUUID();
        return new CategoryDTO(randomName, parent);
    }

    public static CategoryDTO createWithoutName() {
        return new CategoryDTO("", null);
    }

    public static CategoryDTO createWithInvalidParent() {
        CategoryDTO invalidParent = new CategoryDTO();
        invalidParent.setId(999999L);
        return new CategoryDTO("Invalid Parent", invalidParent);
    }
}