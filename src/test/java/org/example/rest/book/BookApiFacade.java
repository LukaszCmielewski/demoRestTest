package org.example.rest.book;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.example.rest.author.AuthorApiFacade;
import org.example.rest.author.AuthorDTO;
import org.example.rest.category.CategoryApiFacade;
import org.example.rest.category.CategoryDTO;
import org.example.rest.publisher.PublisherApiFacade;
import org.example.rest.publisher.PublisherDTO;
import org.example.rest.util.JsonUtils;

import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.given;

public class BookApiFacade {
    static {
        RestAssured.baseURI = "http://localhost:8080/api/book";
    }

    public static Response create(BookDTO dto) {
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

    public static Response update(Long id, BookDTO dto) {
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

    public static void cleanAll() {
        Response response = BookApiFacade.getAll();
        response.then().statusCode(200);

        List<Integer> ids = response.jsonPath().getList("id");
        if (ids != null) {
            for (int i = ids.size() - 1; i >= 0; i--) {
                Integer idInt = ids.get(i);
                Long id = idInt.longValue();
                BookApiFacade.delete(id).then().statusCode(204);
            }
        }
        PublisherApiFacade.cleanAllPublisher();
        AuthorApiFacade.cleanAllAuthors();
        CategoryApiFacade.cleanAllCategories();
    }

    public static BookDTO createFullBookByRest(BookDTO dto) {
        //add categories
        List<CategoryDTO> categories = new ArrayList<>();
        for (CategoryDTO category : dto.getCategories()) {
            CategoryDTO categoryByRest = CategoryApiFacade.createByRest(category);
            categories.add(categoryByRest);
        }
        //add authors
        List<AuthorDTO> authors = new ArrayList<>();
        for (AuthorDTO author : dto.getAuthors()) {
            AuthorDTO authorByRest = AuthorApiFacade.createByRest(author);
            authors.add(authorByRest);
        }
        //add publisher
        PublisherDTO publisher = PublisherApiFacade.createByRest(dto.getPublisher());
        //add book
        dto.setCategories(categories);
        dto.setAuthors(authors);
        dto.setPublisher(publisher);
        System.out.println("Proto book:"+dto);
        Response response = given()
                .contentType(ContentType.JSON)
                .accept("*/*")
                .body(dto)
                .when()
                .post().then().extract().response();
        System.out.println("Status code: "+ response.statusCode());
        response.prettyPrint();
        return JsonUtils.fromJson(response.getBody().asString(), BookDTO.class);

    }
}
