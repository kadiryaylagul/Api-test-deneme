package kloiaApiTest;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import utilities.ConfigurationReader;

import java.util.Random;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class Task {

    @BeforeClass
    public void beforeclass() {
        baseURI = ConfigurationReader.get("pet_url");
    }

    Long createdId;

    @Test
    public void test1() {


        String jsonBody = "{\n" +
                "\"category\": {\n" +
                "\"id\": 0,\n" +
                "\"name\": \"Pets\"\n" +
                "},\n" +
                "\"name\": \"Scout\",\n" +
                "\"photoUrls\": [\n" +
                "\"scout.png\"\n" +
                "],\n" +
                "\"tags\": [\n" +
                "{\n" +
                "\"id\": 0,\n" +
                "\"name\": \"pet-dog\"\n" +
                "}\n" +
                "],\n" +
                "\"status\": \"available\"\n" +
                "}";

        Response response = given().log().all()
                .accept(ContentType.JSON)
                .and()
                .contentType(ContentType.JSON)
                .and()
                .body(jsonBody)
                .when()
                .post("/pet");


        assertEquals(response.statusCode(), 200);
        Assert.assertTrue(response.headers().hasHeaderWithName("Date"));
        assertEquals(response.contentType(), "application/json");
        assertEquals(response.path("name"), "Scout");

        createdId = response.path("id");
        given().accept(ContentType.JSON)
                .pathParam("id", createdId)
                .when().get("/pet/{id}")
                .then().statusCode(200);
    }

    @Test
        public void test2(){



        long id = createdId;

        given().accept(ContentType.JSON)
                .and().pathParam("id",id)
                .when().get("/pet/{id}")
                .then().statusCode(200)
                .and().assertThat().contentType(equalTo("application/json"))
                .and().header("Date",notNullValue())
                .and().assertThat().body("id",equalTo(id),
                "category.id",equalTo(0),
                "category.name",equalTo("Pets"),
                "name",equalTo("Scout"),
                "photoUrls[0]",equalTo("scout.png"),
                "tags.id[0]",equalTo(0),"tags.name[0]",equalTo("pet-dog"),
                "status",equalTo("available"));



    }

    @Test
        public void test3(){


        Long idToDelete = createdId;


        given().
                pathParam("id",idToDelete)
                .when()
                .delete("/pet/{id}")
                .then()
                .statusCode(200).log().all()
                .and()
                .contentType("application/json")
                .and()
                .assertThat().body("message",equalTo(idToDelete),
                "type",equalTo("unknown"));






        }


}
