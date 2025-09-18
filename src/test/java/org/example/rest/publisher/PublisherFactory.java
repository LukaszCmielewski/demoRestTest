package org.example.rest.publisher;

import com.github.javafaker.Faker;

public class PublisherFactory {
    private static final Faker faker = new Faker();

    public static PublisherDTO create(PublisherDTO publisher) {
        return new PublisherDTO(
                publisher.getName(),
                publisher.getAddress(),
                publisher.getContactInfo());
    }

    public static PublisherDTO createRandomPublisher() {
        return PublisherDTO.builder()
                .name(faker.company().name())
                .address(faker.address().fullAddress())
                .contactInfo(faker.phoneNumber().cellPhone())
                .build();
    }
}
