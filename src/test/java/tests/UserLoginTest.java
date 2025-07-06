package tests;

import api.UserClient;
import io.qameta.allure.Step;
import io.qameta.allure.junit5.AllureJunit5;
import models.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import utils.RandomDataGenerator;

import static org.hamcrest.Matchers.equalTo;

@ExtendWith(AllureJunit5.class)
public class UserLoginTest {
    private UserClient userClient;
    private User user;
    private String accessToken;

    @BeforeEach
    public void setUp() {
        userClient = new UserClient();
        user = new User(
                RandomDataGenerator.generateRandomEmail(),
                RandomDataGenerator.generateRandomPassword(),
                RandomDataGenerator.generateRandomName()
        );

        userClient.createUser(user);
        accessToken = userClient.loginUser(user).path("accessToken");
    }

    @AfterEach
    public void tearDown() {
        userClient.deleteUser(accessToken);
    }

    @Test
    @Step("Логин под существующим пользователем")
    public void userCanLoginWithValidCredentials() {
        userClient.loginUser(user)
                .then()
                .statusCode(200)
                .body("success", equalTo(true));
    }

    @Test
    @Step("Логин с неверными учетными данными")
    public void userCannotLoginWithInvalidCredentials() {
        User invalidUser = new User(
                user.getEmail(),
                "wrong_password",
                user.getName()
        );

        userClient.loginUser(invalidUser)
                .then()
                .statusCode(401)
                .body("message", equalTo("email or password are incorrect"));
    }
}