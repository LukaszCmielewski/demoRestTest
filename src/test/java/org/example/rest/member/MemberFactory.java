package org.example.rest.member;

import com.github.javafaker.Faker;

public class MemberFactory {
    private static final Faker faker = new Faker();

    public static MemberDTO create(MemberDTO dto) {
        return new MemberDTO(
                dto.getFirstName(),
                dto.getLastName(),
                dto.getAddress(),
                dto.getPhone(),
                dto.getEmail());
    }

    public static MemberDTO createRandom() {
        return MemberDTO.builder()
                .firstName(faker.name().firstName())
                .lastName(faker.name().lastName())
                .address(faker.address().fullAddress())
                .phone(faker.phoneNumber().cellPhone())
                .email(faker.internet().safeEmailAddress())
                .build();
    }
}
