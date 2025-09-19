package org.example.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.restassured.RestAssured;
import io.restassured.config.ObjectMapperConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;
import org.example.rest.author.AuthorApiFacade;
import org.example.rest.book.BookApiFacade;
import org.example.rest.book.BookDTO;
import org.example.rest.book.BookFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;

public class BookApiTest {
    @Test
    public void runn(){
        System.out.println("Book:" + dto);
    }
    private static Long dtoID;
    private final List<Long> createdIds = new ArrayList<>();
    private static BookDTO dto;
    @AfterEach
    public void cleanAfter() {
        BookApiFacade.cleanAll();
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
        BookDTO book = BookFactory.createRandom();
        System.out.println("DTO: "+book.prettyPrint());
        dto = BookApiFacade.createFullBookByRest(book);
        System.out.println(dto);

        dtoID = dto.getId();
        createdIds.add(dtoID);

    }

    @Test
    public void shouldMatchJsonSchema() {
        InputStream schemaStream = getClass().getClassLoader()
                .getResourceAsStream("schemas/book-schema.json");
        if (schemaStream == null) {
            throw new RuntimeException("Nie znaleziono pliku schematu JSON.");
        }
        Response response = BookApiFacade.getById(createdIds.getFirst());
        response.then()
                .assertThat()
                .body(JsonSchemaValidator.matchesJsonSchema(schemaStream));
    }

    @Test
    public void shouldMatchListSchema() {
        InputStream schemaStream = getClass().getClassLoader()
                .getResourceAsStream("schemas/book-list-schema.json");

        if (schemaStream == null) {
            throw new RuntimeException("Nie znaleziono schematu JSON listy kategorii.");
        }

        Response response = BookApiFacade.getAll();
        response.then().statusCode(200);

        assertThat(response.asString(), JsonSchemaValidator.matchesJsonSchema(schemaStream));
    }
}
