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
public class UserDataUpdateTest {
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
        if (accessToken != null) {
            userClient.deleteUser(accessToken);
        }
    }

    @Test
    @Step("Изменение данных пользователя с авторизацией")
    public void userCanUpdateDataWithAuthorization() {
        User updatedUser = new User(
                RandomDataGenerator.generateRandomEmail(),
                RandomDataGenerator.generateRandomPassword(),
                RandomDataGenerator.generateRandomName()
        );

        userClient.updateUser(accessToken, updatedUser)
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("user.email", equalTo(updatedUser.getEmail()))
                .body("user.name", equalTo(updatedUser.getName()));
    }

    @Test
    @Step("Изменение данных пользователя без авторизации")
    public void userCannotUpdateDataWithoutAuthorization() {
        User updatedUser = new User(
                RandomDataGenerator.generateRandomEmail(),
                RandomDataGenerator.generateRandomPassword(),
                RandomDataGenerator.generateRandomName()
        );

        userClient.updateUser("", updatedUser)
                .then()
                .statusCode(401)
                .body("message", equalTo("You should be authorised"));
    }
}