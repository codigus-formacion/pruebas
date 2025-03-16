package es.codeurjc.board;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import io.restassured.RestAssured;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BoardTest {
    
    @LocalServerPort
    int port;
    
    @BeforeEach
    public void setUp() {
        RestAssured.port = port;
    }
    
    @Test
    public void addPostTest() {
        given().
            contentType("application/json").
            body("{\"username\":\"Michel\",\"title\":\"Vendo coche\",\"text\":\"Seat Panda\"}").
        when().
            post("/posts/").
        then().
            statusCode(201).
            body("username", equalTo("Michel")).
            body("title", equalTo("Vendo coche")).
            body("text", equalTo("Seat Panda"));
    } 

    @Test
    public void getOnePostTest() {
        Integer id = createPostAndGetId();
        
        given().
            contentType("application/json").
        when().
            get("/posts/{id}", id).
        then().
            statusCode(200).
            body("username", equalTo("Michel")).
            body("title", equalTo("Vendo coche")).
            body("text", equalTo("Seat Panda"));
    }

    @Test
    public void getOnePostNotFoundTest() {
        given().
            contentType("application/json").
        when().
            get("/posts/{id}", 999).
        then().
            statusCode(404);
    }

    @Test
    public void deletePostTest() {
        Integer id = createPostAndGetId();
        
        given().
            contentType("application/json").
        when().
            delete("/posts/{id}", id).
        then().
            statusCode(200);
    }

    @Test
    public void deletePostNotFoundTest() {
        given().
            contentType("application/json").
        when().
            delete("/posts/{id}", 999).
        then().
            statusCode(404);
    }

    @Test
    public void updatePostTest() {
        Integer id = createPostAndGetId();
        
        given().
            contentType("application/json").
            body("{\"username\":\"Michel\",\"title\":\"Vendo moto\",\"text\":\"Yamaha\"}").
        when().
            put("/posts/{id}", id).
        then().
            statusCode(200).
            body("username", equalTo("Michel")).
            body("title", equalTo("Vendo moto")).
            body("text", equalTo("Yamaha"));
    }

    private Integer createPostAndGetId() {
        Integer id = given().
            contentType("application/json").
            body("{\"username\":\"Michel\",\"title\":\"Vendo coche\",\"text\":\"Seat Panda\"}").
        when().
            post("/posts/").andReturn().jsonPath().get("id");
        return id;
    }

}
