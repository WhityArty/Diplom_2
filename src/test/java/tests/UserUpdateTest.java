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

public class UserUpdateTest {

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
    @DisplayName("Изменение имени с авторизацией")
    public void updateNameWithAuthTest() {
        User updatedUser = new User(user.getEmail(), user.getPassword(), "New Name");
        Response response = userClient.updateUser(updatedUser, accessToken);
        response.then()
                .statusCode(SC_OK)
                .body("success", equalTo(true))
                .body("user.name", equalTo(updatedUser.getName()));
    }

    @Test
    @DisplayName("Изменение пароля с авторизацией")
    public void updatePasswordWithAuthTest() {
        User updatedUser = new User(user.getEmail(), "newpassword123", user.getName());
        Response response = userClient.updateUser(updatedUser, accessToken);
        response.then()
                .statusCode(SC_OK)
                .body("success", equalTo(true));
    }

    @Test
    @DisplayName("Попытка изменения email с авторизацией")
    public void updateEmailWithAuthTest() {
        User updatedUser = new User("newemail@test.com", user.getPassword(), user.getName());
        Response response = userClient.updateUser(updatedUser, accessToken);

        if (response.getStatusCode() == SC_OK) {
            response.then()
                    .body("success", equalTo(true))
                    .body("user.email", equalTo(updatedUser.getEmail()));
        } else {
            response.then()
                    .statusCode(SC_FORBIDDEN)
                    .body("success", equalTo(false));
        }
    }

    @Test
    @DisplayName("Изменение данных без авторизации")
    public void updateUserWithoutAuthTest() {
        User updatedUser = new User(user.getEmail(), "newpassword", "New Name");
        Response response = userClient.updateUser(updatedUser, null);
        response.then()
                .statusCode(SC_UNAUTHORIZED)
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"));
    }
}