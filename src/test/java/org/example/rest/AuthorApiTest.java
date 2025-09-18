package org.example.rest;

import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import org.example.rest.author.AuthorApiFacade;
import org.example.rest.author.AuthorDTO;
import org.example.rest.author.AuthorFactory;
import org.example.rest.category.CategoryApiFacade;
import org.junit.jupiter.api.*;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

//@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AuthorApiTest {
    private final List<Long> createdAuthorIds = new ArrayList<>();
    private static Long authorID;
    private static AuthorDTO author;

    @AfterEach
    public void cleanAfter() {
        AuthorApiFacade.cleanAllAuthors();
    }

    @BeforeEach
    public void setup() {
        AuthorDTO autor = AuthorFactory.createRandomAuthor();
        Response response = AuthorApiFacade.create(autor);
        response.then().statusCode(201);
        author = response.as(AuthorDTO.class);
        authorID = ((Number) response.path("id")).longValue();
        createdAuthorIds.add(authorID);
    }

    @Test
    public void getAuthorById() {
        Response response = AuthorApiFacade.getById(authorID)
                .then()
                .statusCode(200).extract().response();
        Long idFromResponse = ((Number) response.path("id")).longValue();
        Assertions.assertEquals(authorID, idFromResponse);
        response.prettyPrint();
    }

    @Test
    public void getInvalidAuthorById() {
        Response response = AuthorApiFacade.getById(authorID+9999)
                .then()
                .statusCode(404).extract().response();
        response.prettyPrint();
    }

    @Test
    public void createAuthor(){
        AuthorDTO newAuthor= AuthorDTO.builder()
                .firstName("Łukasz")
                .lastName("Ćmielewski")
                .biography("Nic ważnego")
                .build();
        Response response = AuthorApiFacade.create(newAuthor);
        response.then().statusCode(201);
        response.prettyPrint();
        Assertions.assertEquals(newAuthor.getFirstName(), response.path("firstName"));
        Assertions.assertEquals(newAuthor.getLastName(), response.path("lastName"));
        Assertions.assertEquals(newAuthor.getBiography(), response.path("biography"));
    }

    @Test
    public void updateAuthor(){
        System.out.println(author.prettyPrint());
        AuthorDTO newAuthor= AuthorDTO.builder()
                .firstName(author.getFirstName()+"_UPDATED")
                .lastName(author.getLastName()+"_UPDATED")
                .biography(author.getBiography()+"_UPDATED")
                .build();
        Response response = AuthorApiFacade.update(author.getId(), newAuthor);
        response.then().statusCode(200);
        response.prettyPrint();
        Assertions.assertEquals(author.getFirstName()+"_UPDATED", response.path("firstName"));
        Assertions.assertEquals(author.getLastName()+"_UPDATED", response.path("lastName"));
        Assertions.assertEquals(author.getBiography()+"_UPDATED", response.path("biography"));

    }

    @Test
    public void deleteAuthor(){
        Response resp = AuthorApiFacade.delete(author.getId());
        resp.then().statusCode(204);
        resp.prettyPrint();
    }

    @Test
    public void shouldMatchJsonSchema() {
        InputStream schemaStream = getClass().getClassLoader()
                .getResourceAsStream("schemas/author-schema.json");
        if (schemaStream == null) {
            throw new RuntimeException("Nie znaleziono pliku schematu JSON.");
        }
        Response response = AuthorApiFacade.getById(createdAuthorIds.getFirst());
        response.then()
                .assertThat()
                .body(JsonSchemaValidator.matchesJsonSchema(schemaStream));
    }

    @Test
    public void shouldMatchListSchema() {
        InputStream schemaStream = getClass().getClassLoader()
                .getResourceAsStream("schemas/author-list-schema.json");

        if (schemaStream == null) {
            throw new RuntimeException("Nie znaleziono schematu JSON listy kategorii.");
        }

        Response response = AuthorApiFacade.getAll();
        response.then().statusCode(200);

        assertThat(response.asString(), JsonSchemaValidator.matchesJsonSchema(schemaStream));
    }
}
