package org.example.rest.member;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.example.rest.util.JsonUtils;

import java.util.List;

import static io.restassured.RestAssured.given;

public class MemberApiFacade {
    static {
        RestAssured.baseURI = "http://localhost:8080/api/member";
    }

    public static Response create(MemberDTO dto) {
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

    public static Response update(Long id, MemberDTO dto) {
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
        Response response = MemberApiFacade.getAll();
        response.then().statusCode(200);

        List<Integer> ids = response.jsonPath().getList("id");
        if (ids != null) {
            // Usuwamy każdą kategorię, w kolejności od końca by uniknąć problemów z parent-child
            for (int i = ids.size() - 1; i >= 0; i--) {
                Integer idInt = ids.get(i);
                Long id = idInt.longValue();
                MemberApiFacade.delete(id).then().statusCode(204);
            }
        }
    }

    public static MemberDTO createByRest(MemberDTO dto) {
        Response response = given().contentType(ContentType.JSON)
                .body(dto)
                .when()
                .post().then().statusCode(201).extract().response();
        response.prettyPrint();
        return JsonUtils.fromJson(response.getBody().asString(), MemberDTO.class);
    }
}
