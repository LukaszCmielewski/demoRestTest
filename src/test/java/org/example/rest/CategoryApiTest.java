package org.example.rest;

import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;
import org.example.rest.category.CategoryApiFacade;
import org.example.rest.category.CategoryDto;
import org.example.rest.category.CategoryFactory;
import org.junit.jupiter.api.*;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;


@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CategoryApiTest {

    private static CategoryDto parent;
    private static Long parentId;
    private static Long childId;
    private final List<Long> createdCategoryIds = new ArrayList<>();

    @BeforeEach
    public void setup() {
        CategoryDto parent = CategoryFactory.createWithRandomName(null);
        Response response = CategoryApiFacade.create(parent);
        response.then().statusCode(201);
        parentId = ((Number) response.path("id")).longValue();
        parent.setId(parentId);
        createdCategoryIds.add(parentId);
    }

    @AfterEach
    public void cleanAfter() {
        CategoryApiFacade.cleanAllCategories();

    }

    @Test
    public void createParentCategory() {
        String name = "newCategory" + System.currentTimeMillis();
        CategoryDto newParent = CategoryDto.builder().name(name).build();
        CategoryApiFacade.create(newParent)
                .then().statusCode(201)
                .body("name", equalTo(newParent.getName()))
                .body("parentCategory", nullValue());
    }

    @Test
    public void createSubCategory() {
        long currented = System.currentTimeMillis();
        String pName = "Fasntasy " + currented;
        String sName = "Dark Fantasy " + currented;
        CategoryDto pCategory = CategoryApiFacade.createCategoryByRest(
                CategoryDto
                        .builder()
                        .name(pName)
                        .build()
        );

        CategoryDto build = CategoryDto.builder().name(sName).parentCategory(pCategory).build();

        Response sCategory = CategoryApiFacade.create(build)
                .then()
                .statusCode(201)
                .body("name", equalTo(sName))
                .body("parentCategory.name", equalTo(pName))
                .extract()
                .response();
    }

    @Test
    public void deleteCategory() {
        CategoryApiFacade.delete(createdCategoryIds.getFirst()).then().statusCode(204).extract().response();
    }

    @Test
    public void shouldMatchCategoryJsonSchema() {
        InputStream schemaStream = getClass().getClassLoader()
                .getResourceAsStream("schemas/category-schema.json");
        if (schemaStream == null) {
            throw new RuntimeException("Nie znaleziono pliku schematu JSON.");
        }
        Response response = CategoryApiFacade.getById(createdCategoryIds.getFirst());
        response.then()
                .assertThat()
                .body(JsonSchemaValidator.matchesJsonSchema(schemaStream));
    }

    @Test
    public void shouldMatchCategoryListSchema() {
        InputStream schemaStream = getClass().getClassLoader()
                .getResourceAsStream("schemas/category-list-schema.json");

        if (schemaStream == null) {
            throw new RuntimeException("Nie znaleziono schematu JSON listy kategorii.");
        }

        Response response = CategoryApiFacade.getAll();
        response.then().statusCode(200);

        assertThat(response.asString(), JsonSchemaValidator.matchesJsonSchema(schemaStream));
    }


    @Test
    public void get() {
        CategoryDto withRandomName = CategoryFactory.createWithRandomName(null);
        CategoryDto categoryByRest = CategoryApiFacade.createCategoryByRest(withRandomName);
        System.out.println(categoryByRest.getName());
    }
}
