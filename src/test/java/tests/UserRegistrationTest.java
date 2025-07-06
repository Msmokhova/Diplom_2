package tests;

import api.UserClient;
import io.qameta.allure.Step;
import io.qameta.allure.junit5.AllureJunit5;
import models.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import utils.RandomDataGenerator;
import org.junit.jupiter.params.ParameterizedTest;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.equalTo;

@ExtendWith(AllureJunit5.class)
public class UserRegistrationTest {
    private UserClient userClient;
    private User user;
    private String accessToken;

    @BeforeEach
    public void setUp() {
        userClient = new UserClient();
    }

    @AfterEach
    public void tearDown() {
        if (accessToken != null) {
            userClient.deleteUser(accessToken);
        }
    }

    @Test
    @DisplayName("Создание уникального пользователя")
    @Step("Создание уникального пользователя")
    public void userCanBeCreatedWithValidData() {
        user = new User(
                RandomDataGenerator.generateRandomEmail(),
                RandomDataGenerator.generateRandomPassword(),
                RandomDataGenerator.generateRandomName()
        );

        userClient.createUser(user)
                .then()
                .statusCode(200)
                .body("success", equalTo(true));
    }

    @Test
    @DisplayName("Создание уже зарегистрированного пользователя")
    @Step("Создание уже зарегистрированного пользователя")
    public void userCannotBeCreatedTwice() {
        user = new User(
                RandomDataGenerator.generateRandomEmail(),
                RandomDataGenerator.generateRandomPassword(),
                RandomDataGenerator.generateRandomName()
        );

        userClient.createUser(user);
        accessToken = userClient.loginUser(user).path("accessToken");

        userClient.createUser(user)
                .then()
                .statusCode(403)
                .body("message", equalTo("User already exists"));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("invalidUserDataProvider")
    @DisplayName("Создание пользователя с невалидными данными")
    @Step("Создание пользователя с невалидными данными")
    public void userCannotBeCreatedWithInvalidData(String testName, User testUser) {
        userClient.createUser(testUser)
                .then()
                .statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }

    private static Stream<Arguments> invalidUserDataProvider() {
        return Stream.of(
                Arguments.of("Без имени",
                        new User(
                                RandomDataGenerator.generateRandomEmail(),
                                RandomDataGenerator.generateRandomPassword(),
                                null
                        )),
                Arguments.of("Без почты",
                        new User(
                                null,
                                RandomDataGenerator.generateRandomPassword(),
                                RandomDataGenerator.generateRandomName()
                        )),
                Arguments.of("Без пароля",
                        new User(
                                RandomDataGenerator.generateRandomEmail(),
                                null,
                                RandomDataGenerator.generateRandomName()
                        )),
                Arguments.of("Пустой объект", new User())
        );
    }
}