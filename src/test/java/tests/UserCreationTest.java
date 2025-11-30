package tests;

import clients.UserClient;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import models.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import utils.DataGenerator;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.*;

public class UserCreationTest {

    private UserClient userClient;
    private User user;
    private String accessToken;

    @Before
    public void setUp() {
        userClient = new UserClient();
        user = DataGenerator.getRandomUser();
    }

    @After
    public void tearDown() {
        if (accessToken != null) {
            userClient.deleteUser(accessToken);
        }
    }

    @Test
    @DisplayName("Создание уникального пользователя")
    public void createUniqueUserTest() {
        Response response = userClient.createUser(user);
        response.then()
                .statusCode(SC_OK)
                .body("success", equalTo(true));

        accessToken = response.path("accessToken");
    }

    @Test
    @DisplayName("Создание пользователя, который уже зарегистрирован")
    public void createDuplicateUserTest() {
        // Создаем первого пользователя
        Response firstResponse = userClient.createUser(user);
        accessToken = firstResponse.path("accessToken");

        // Пытаемся создать такого же пользователя
        Response secondResponse = userClient.createUser(user);
        secondResponse.then()
                .statusCode(SC_FORBIDDEN)
                .body("success", equalTo(false))
                .body("message", equalTo("User already exists"));
    }

    @Test
    @DisplayName("Создание пользователя без email")
    public void createUserWithoutEmailTest() {
        User userWithoutEmail = DataGenerator.getUserWithoutEmail();
        Response response = userClient.createUser(userWithoutEmail);
        response.then()
                .statusCode(SC_FORBIDDEN)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Создание пользователя без пароля")
    public void createUserWithoutPasswordTest() {
        User userWithoutPassword = DataGenerator.getUserWithoutPassword();
        Response response = userClient.createUser(userWithoutPassword);
        response.then()
                .statusCode(SC_FORBIDDEN)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Создание пользователя без имени")
    public void createUserWithoutNameTest() {
        User userWithoutName = DataGenerator.getUserWithoutName();
        Response response = userClient.createUser(userWithoutName);
        response.then()
                .statusCode(SC_FORBIDDEN)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }
}