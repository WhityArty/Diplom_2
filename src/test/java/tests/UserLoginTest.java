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

public class UserLoginTest {

    private UserClient userClient;
    private User user;
    private String accessToken;

    @Before
    public void setUp() {
        userClient = new UserClient();
        user = DataGenerator.getRandomUser();

        // Создаем пользователя для тестов
        Response response = userClient.createUser(user);
        accessToken = response.path("accessToken");
    }

    @After
    public void tearDown() {
        if (accessToken != null) {
            userClient.deleteUser(accessToken);
        }
    }

    @Test
    @DisplayName("Логин под существующим пользователем")
    public void loginWithExistingUserTest() {
        Response response = userClient.loginUser(user);
        response.then()
                .statusCode(SC_OK)
                .body("success", equalTo(true))
                .body("accessToken", notNullValue())
                .body("refreshToken", notNullValue());
    }

    @Test
    @DisplayName("Логин с неверным email")
    public void loginWithWrongEmailTest() {
        User wrongUser = new User("wrong@email.com", user.getPassword(), user.getName());
        Response response = userClient.loginUser(wrongUser);
        response.then()
                .statusCode(SC_UNAUTHORIZED)
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));
    }

    @Test
    @DisplayName("Логин с неверным паролем")
    public void loginWithWrongPasswordTest() {
        User wrongUser = new User(user.getEmail(), "wrongpassword", user.getName());
        Response response = userClient.loginUser(wrongUser);
        response.then()
                .statusCode(SC_UNAUTHORIZED)
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));
    }
}