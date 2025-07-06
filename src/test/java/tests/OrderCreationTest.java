package tests;

import api.*;
import io.qameta.allure.Step;
import io.qameta.allure.junit5.AllureJunit5;
import io.restassured.response.Response;
import models.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import utils.RandomDataGenerator;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;

@ExtendWith(AllureJunit5.class)
public class OrderCreationTest {
    private UserClient userClient;
    private AuthClient authClient;
    private OrderClient orderClient;
    private User user;
    private String accessToken;
    private List<String> validIngredientIds;

    @BeforeEach
    public void setUp() {
        userClient = new UserClient();
        authClient = new AuthClient();
        orderClient = new OrderClient();

        user = new User(
                RandomDataGenerator.generateRandomEmail(),
                RandomDataGenerator.generateRandomPassword(),
                RandomDataGenerator.generateRandomName()
        );

        Response createResponse = userClient.createUser(user);
        System.out.println("User creation response: " + createResponse.asString());

        Response loginResponse = authClient.login(user);
        System.out.println("Login response: " + loginResponse.asString());
        accessToken = loginResponse.path("accessToken");

        validIngredientIds = orderClient.getAvailableIngredientIds();
        System.out.println("Available ingredients: " + validIngredientIds);

    }

    @AfterEach
    public void tearDown() {
        if (accessToken != null) {
            userClient.deleteUser(accessToken);
        }
    }

    @Test
    @Step("Создание заказа с авторизацией и ингредиентами")
    public void orderCanBeCreatedWithAuthAndIngredients() {
        String bunId = validIngredientIds.stream()
                .findFirst()
                .orElseThrow();
        String fillingId = validIngredientIds.stream()
                .skip(1)
                .findFirst()
                .orElseThrow();

        Order order = new Order(Arrays.asList(bunId, fillingId));

        orderClient.createOrder(order, accessToken)
                .then()
                .statusCode(200)
                .body("success", equalTo(true));
    }

    @Test
    @Step("Создание заказа без авторизации")
    public void orderCanBeCreatedWithoutAuth() {
        List<String> ingredients = validIngredientIds.subList(0, 1);

        Order order = new Order(ingredients);

        orderClient.createOrderWithoutAuth(order)
                .then()
                .statusCode(200)
                .body("success", equalTo(true));
    }

    @Test
    @Step("Создание заказа без ингредиентов")
    public void orderCannotBeCreatedWithoutIngredients() {
        Order order = new Order(null);

        orderClient.createOrder(order, accessToken)
                .then()
                .statusCode(400)
                .body("success", equalTo(false))
                .body("message", equalTo("Ingredient ids must be provided"));
    }

    @Test
    @Step("Создание заказа с неверным хешем ингредиентов")
    public void orderCannotBeCreatedWithInvalidIngredientHash() {
        List<String> invalidIngredients = List.of(
                "invalid_hash_" + System.currentTimeMillis(),
                "invalid_hash_" + (System.currentTimeMillis() + 1)
        );

        Order order = new Order(invalidIngredients);

        orderClient.createOrder(order, accessToken)
                .then()
                .statusCode(500);
    }


}