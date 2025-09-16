package org.example.rest.author;

import com.github.javafaker.Faker;

public class AuthorFactory {
    private static final Faker faker = new Faker();

    public static AuthorDTO create(AuthorDTO author) {
        return new AuthorDTO(author.getFirstName(), author.getLastName(), author.getBiography());
    }

    public static AuthorDTO createRandomAuthor() {
        return AuthorDTO.builder()
                .firstName(faker.name().firstName())
                .lastName(faker.name().lastName())
                .biography(faker.lorem().paragraph())
                .build();
    }
}
