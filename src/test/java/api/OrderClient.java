package api;

import io.restassured.response.Response;
import models.Ingredient;
import models.Order;

import java.util.List;


import static io.restassured.RestAssured.given;

public class OrderClient {
    private static final String BASE_URL = "https://stellarburgers.nomoreparties.site/api";

    public Response createOrder(Order order, String accessToken) {
        return given()
                .header("Content-type", "application/json")
                .header("Authorization", accessToken)
                .baseUri(BASE_URL)
                .body(order)
                .when()
                .post("/orders");
    }

    public Response createOrderWithoutAuth(Order order) {
        return given()
                .header("Content-type", "application/json")
                .baseUri(BASE_URL)
                .body(order)
                .when()
                .post("/orders");
    }

    public Response getUserOrders(String accessToken) {
        return given()
                .header("Authorization", accessToken)
                .baseUri(BASE_URL)
                .when()
                .get("/orders");
    }
    public List<Ingredient> getIngredients() {
        return given()
                .baseUri(BASE_URL)
                .when()
                .get("/ingredients")
                .then()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getList("data", Ingredient.class);
    }

    public List<String> getAvailableIngredientIds() {
        return given()
                .baseUri(BASE_URL)
                .when()
                .get("/ingredients")
                .then()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getList("data._id");
    }
}