package api;

import io.restassured.response.Response;
import models.User;

import static io.restassured.RestAssured.given;

public class AuthClient {
    private static final String BASE_URL = "https://stellarburgers.nomoreparties.site/api";

    public Response login(User user) {
        return given()
                .header("Content-type", "application/json")
                .baseUri(BASE_URL)
                .body(user)
                .when()
                .post("/auth/login");
    }

    public Response logout(String refreshToken) {
        return given()
                .header("Content-type", "application/json")
                .baseUri(BASE_URL)
                .body("{\"token\":\"" + refreshToken + "\"}")
                .when()
                .post("/auth/logout");
    }
}