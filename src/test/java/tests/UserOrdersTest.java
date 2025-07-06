package tests;

import api.AuthClient;
import api.OrderClient;
import api.UserClient;
import io.qameta.allure.Step;
import io.qameta.allure.junit5.AllureJunit5;
import models.Order;
import models.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import utils.RandomDataGenerator;

import java.util.List;

import static org.hamcrest.Matchers.equalTo;

@ExtendWith(AllureJunit5.class)
public class UserOrdersTest {
    private UserClient userClient;
    private AuthClient authClient;
    private OrderClient orderClient;
    private User user;
    private String accessToken;

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

        userClient.createUser(user);
        accessToken = authClient.login(user).path("accessToken");

        Order order = new Order(List.of("61c0c5a71d1f82001bdaaa6d", "61c0c5a71d1f82001bdaaa6f"));

        orderClient.createOrder(order, accessToken);
    }

    @AfterEach
    public void tearDown() {
        if (accessToken != null) {
            userClient.deleteUser(accessToken);
        }
    }

    @Test
    @Step("Получение заказов авторизованного пользователя")
    public void userCanGetOrdersWithAuth() {
        orderClient.getUserOrders(accessToken)
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("orders.size()", equalTo(1));
    }

    @Test
    @Step("Получение заказов неавторизованного пользователя")
    public void userCannotGetOrdersWithoutAuth() {
        orderClient.getUserOrders("")
                .then()
                .statusCode(401)
                .body("message", equalTo("You should be authorised"));
    }
}