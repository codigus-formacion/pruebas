package es.codeurjc.board;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import org.json.JSONException;
import org.json.JSONObject;
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
    public void addPostTest_withJSONObject() throws JSONException {

        JSONObject body = new JSONObject();
        body.put("username", "Michel");
        body.put("title", "Vendo coche");
        body.put("text", "Seat Panda");
		
		given().
			contentType("application/json").
			body(body.toString()).
		when().
			post("/posts/").
		then().
			statusCode(201).
			body("username", equalTo(body.get("username"))).
            body("title", equalTo(body.get("title"))).
            body("text", equalTo(body.get("text")));
    } 
    
    
}
