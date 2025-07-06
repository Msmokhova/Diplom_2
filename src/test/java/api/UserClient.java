package api;

import io.restassured.response.Response;
import models.User;

import static io.restassured.RestAssured.given;

public class UserClient {
    private static final String BASE_URL = "https://stellarburgers.nomoreparties.site/api";

    public Response createUser(User user) {
        return given()
                .header("Content-type", "application/json")
                .baseUri(BASE_URL)
                .body(user)
                .when()
                .post("/auth/register");
    }

    public Response loginUser(User user) {
        return given()
                .header("Content-type", "application/json")
                .baseUri(BASE_URL)
                .body(user)
                .when()
                .post("/auth/login");
    }

    public Response updateUser(String accessToken, User user) {
        return given()
                .header("Content-type", "application/json")
                .header("Authorization", accessToken)
                .baseUri(BASE_URL)
                .body(user)
                .when()
                .patch("/auth/user");
    }

    public Response deleteUser(String accessToken) {
        return given()
                .header("Authorization", accessToken)
                .baseUri(BASE_URL)
                .when()
                .delete("/auth/user");
    }

    public Response getUserInfo(String accessToken) {
        return given()
                .header("Authorization", accessToken)
                .baseUri(BASE_URL)
                .when()
                .get("/auth/user");
    }
}