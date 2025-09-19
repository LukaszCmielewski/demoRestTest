package org.example.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.restassured.RestAssured;
import io.restassured.config.ObjectMapperConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.http.ContentType;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;

import org.example.rest.member.MemberApiFacade;
import org.example.rest.member.MemberDTO;
import org.example.rest.member.MemberFactory;


import org.example.rest.util.RestAssuredSetup;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;

public class MemberApiTest {
    private final List<Long> createdIds = new ArrayList<>();
    private static Long dtoID;
    private static MemberDTO dto;
    @AfterEach
    public void cleanAfter() {
        MemberApiFacade.cleanAll();
    }

    @BeforeEach
    public void setup() {
        ObjectMapper mapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        RestAssured.config = RestAssuredConfig.config()
                .objectMapperConfig(
                        ObjectMapperConfig.objectMapperConfig()
                                .jackson2ObjectMapperFactory((cls, charset) -> mapper)
                );
        MemberDTO member = MemberFactory.createRandom();
        System.out.println("DTO: "+member.prettyPrint());
        Response response = MemberApiFacade.create(member);
        System.out.println(response.asString());
        response.then().statusCode(201);
        dto = response.as(MemberDTO.class);
        dtoID = ((Number) response.path("id")).longValue();
        createdIds.add(dtoID);
        response.body().prettyPrint();
    }


    @Test
    public void shouldMatchJsonSchema() {
        InputStream schemaStream = getClass().getClassLoader()
                .getResourceAsStream("schemas/member-schema.json");
        if (schemaStream == null) {
            throw new RuntimeException("Nie znaleziono pliku schematu JSON.");
        }
        Response response = MemberApiFacade.getById(createdIds.getFirst());
        response.then()
                .assertThat()
                .body(JsonSchemaValidator.matchesJsonSchema(schemaStream));
    }

    @Test
    public void shouldMatchListSchema() {
        InputStream schemaStream = getClass().getClassLoader()
                .getResourceAsStream("schemas/member-list-schema.json");

        if (schemaStream == null) {
            throw new RuntimeException("Nie znaleziono schematu JSON listy kategorii.");
        }

        Response response = MemberApiFacade.getAll();
        response.then().statusCode(200);

        assertThat(response.asString(), JsonSchemaValidator.matchesJsonSchema(schemaStream));
    }

    @Test
    public void getByID(){
        Response response = MemberApiFacade.getById(dtoID)
                .then()
                .statusCode(200).extract().response();
        Long idFromResponse = ((Number) response.path("id")).longValue();
        Assertions.assertEquals(dtoID, idFromResponse);
        response.prettyPrint();
    }

    @Test
    public void getInvalidById() {
        Response response = MemberApiFacade.getById(dtoID+9999)
                .then()
                .statusCode(404).extract().response();
        response.prettyPrint();
    }

    @Test
    public void create(){
        long currentTime=System.currentTimeMillis();
        MemberDTO newPublisher= MemberFactory.createRandom();
        Response response = MemberApiFacade.create(newPublisher);
        response.then().statusCode(201);
        response.prettyPrint();
        Assertions.assertEquals(newPublisher.getFirstName(), response.path("firstName"));
        Assertions.assertEquals(newPublisher.getLastName(), response.path("lastName"));
        Assertions.assertEquals(newPublisher.getAddress(), response.path("address"));
        Assertions.assertEquals(newPublisher.getPhone(), response.path("phone"));
        Assertions.assertEquals(newPublisher.getEmail(), response.path("email"));

    }

    @Test
    public void Omapp(){
        ObjectMapper mapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        String json = null;
        try {
            json = mapper.writeValueAsString(LocalDate.of(2023, 9, 15));
            System.out.println(json); // powinno wypisać: "2023-09-15"
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }

    @Test
    public void update(){
        System.out.println("Old:\n"+dto);
        dto.setAddress(dto.getAddress()+"_UPD");
        System.out.println("New: \n"+dto);
        Response response = MemberApiFacade.update( dto.getId(), dto);
        response.then().statusCode(200);
        response.prettyPrint();
        Assertions.assertEquals(dto.getAddress(), response.path("address"));
    }

    @Test
    public void delete(){
        Response resp = MemberApiFacade.delete((long) dto.getId());
        resp.then().statusCode(204);
        resp.prettyPrint();
    }
}
