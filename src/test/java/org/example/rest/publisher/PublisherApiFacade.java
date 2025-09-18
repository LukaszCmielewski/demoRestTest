package org.example.rest.publisher;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.example.rest.author.AuthorApiFacade;
import org.example.rest.author.AuthorDTO;
import org.example.rest.util.JsonUtils;

import java.util.List;

import static io.restassured.RestAssured.given;

public class PublisherApiFacade {
    static {
        RestAssured.baseURI = "http://localhost:8080/api/publisher";
    }

    public static Response create(PublisherDTO dto) {
        return given()
                .contentType(ContentType.JSON)
                .accept("*/*")
                .body(dto)
                .when()
                .post();
    }

    public static Response getById(Long id) {
        return given()
                .when()
                .get("/{id}", id).then().extract().response();
    }

    public static Response update(Long id, PublisherDTO dto) {
        return given()
                .contentType(ContentType.JSON)
                .body(dto)
                .when()
                .put("/{id}", id);
    }

    public static Response delete(Long id) {
        return given()
                .when()
                .delete("/{id}", id);
    }

    public static Response getAll() {
        return given()
                .when()
                .get("").then().statusCode(200).extract().response();
    }

    public static void cleanAllPublisher() {
        Response response = PublisherApiFacade.getAll();
        response.then().statusCode(200);

        List<Integer> ids = response.jsonPath().getList("id");
        if (ids != null) {
            // Usuwamy każdą kategorię, w kolejności od końca by uniknąć problemów z parent-child
            for (int i = ids.size() - 1; i >= 0; i--) {
                Integer idInt = ids.get(i);
                Long id = idInt.longValue();
                PublisherApiFacade.delete(id).then().statusCode(204);
            }
        }
    }

    public static PublisherDTO createAuthorByRest(PublisherDTO publisher) {
        Response response = given().contentType(ContentType.JSON)
                .body(publisher)
                .when()
                .post().then().statusCode(201).extract().response();
        response.prettyPrint();
        return JsonUtils.fromJson(response.getBody().asString(), PublisherDTO.class);
    }
}
