package org.example.rest;

import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;


import org.example.rest.publisher.PublisherApiFacade;
import org.example.rest.publisher.PublisherDTO;
import org.example.rest.publisher.PublisherFactory;
import org.junit.jupiter.api.*;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
//@Disabled
public class PublisherApiTest {
    private final List<Long> createdPublisherIds = new ArrayList<>();
    private static Long publisherID;
    private static PublisherDTO publisher;
    @AfterEach
    public void cleanAfter() {
        PublisherApiFacade.cleanAllPublisher();
    }

    @BeforeEach
    public void setup() {
        PublisherDTO publisherDto = PublisherFactory.createRandomPublisher();
        System.out.println("PublisherDTO: "+publisherDto.prettyPrint());
        Response response = PublisherApiFacade.create(publisherDto);
        System.out.println(response.asString());
        response.then().statusCode(201);
        publisher = response.as(PublisherDTO.class);
        System.out.println("Publisher response: "+publisher);
        publisherID = ((Number) response.path("id")).longValue();
        createdPublisherIds.add(publisherID);
        response.body().prettyPrint();
    }

    @Test
    public void shouldMatchJsonSchema() {
        InputStream schemaStream = getClass().getClassLoader()
                .getResourceAsStream("schemas/publisher-schema.json");
        if (schemaStream == null) {
            throw new RuntimeException("Nie znaleziono pliku schematu JSON.");
        }
        Response response = PublisherApiFacade.getById(createdPublisherIds.getFirst());
        response.then()
                .assertThat()
                .body(JsonSchemaValidator.matchesJsonSchema(schemaStream));
    }

    @Test
    public void shouldMatchListSchema() {
        InputStream schemaStream = getClass().getClassLoader()
                .getResourceAsStream("schemas/publisher-list-schema.json");

        if (schemaStream == null) {
            throw new RuntimeException("Nie znaleziono schematu JSON listy kategorii.");
        }

        Response response = PublisherApiFacade.getAll();
        response.then().statusCode(200);

        assertThat(response.asString(), JsonSchemaValidator.matchesJsonSchema(schemaStream));
    }

    @Test
    public void getByID(){
        Response response = PublisherApiFacade.getById(publisherID)
                .then()
                .statusCode(200).extract().response();
        Long idFromResponse = ((Number) response.path("id")).longValue();
        Assertions.assertEquals(publisherID, idFromResponse);
        response.prettyPrint();
    }

    @Test
    public void getInvalidById() {
        Response response = PublisherApiFacade.getById(publisherID+9999)
                .then()
                .statusCode(404).extract().response();
        response.prettyPrint();
    }

    @Test
    public void create(){
        long currentTime=System.currentTimeMillis();
        PublisherDTO newPublisher= PublisherDTO.builder()
                .name(publisher.getName()+currentTime)
                .address(publisher.getAddress())
                .contactInfo(publisher.getContactInfo())
                .build();
        System.out.println("Create publisher: "+ newPublisher);
        Response response = PublisherApiFacade.create(newPublisher);
        response.then().statusCode(201);
        response.prettyPrint();
        Assertions.assertEquals(newPublisher.getName(), response.path("name"));
        Assertions.assertEquals(newPublisher.getAddress(), response.path("address"));
        Assertions.assertEquals(newPublisher.getContactInfo(), response.path("contactInfo"));
    }

    @Test
    public void update(){
        System.out.println("Update publisher"+publisher.prettyPrint());
        PublisherDTO newPublisher= PublisherDTO.builder()
                .name(publisher.getName()+"_UPDATED")
                .address(publisher.getAddress()+"_UPDATED")
                .contactInfo(publisher.getContactInfo()+"_UPDATED")
                .build();
        Response response = PublisherApiFacade.update(publisher.getId(), newPublisher);
        response.then().statusCode(200);
        response.prettyPrint();
        Assertions.assertEquals(publisher.getName()+"_UPDATED", response.path("name"));
        Assertions.assertEquals(publisher.getAddress()+"_UPDATED", response.path("address"));
        Assertions.assertEquals(publisher.getContactInfo()+"_UPDATED", response.path("contactInfo"));

    }

    @Test
    public void delete(){
        Response resp = PublisherApiFacade.delete(publisher.getId());
        resp.then().statusCode(204);
        resp.prettyPrint();
    }
}
