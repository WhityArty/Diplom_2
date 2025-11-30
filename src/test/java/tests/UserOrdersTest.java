package tests;

import clients.UserClient;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import models.Order;
import models.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import utils.DataGenerator;

import java.util.Arrays;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.*;

public class UserOrdersTest {

    private UserClient userClient;
    private User user;
    private String accessToken;

    @Before
    public void setUp() {
        userClient = new UserClient();
        user = DataGenerator.getRandomUser();

        // Создаем пользователя и заказ для тестов
        Response response = userClient.createUser(user);
        accessToken = response.path("accessToken");

        // Создаем заказ для пользователя
        Order order = new Order(Arrays.asList(
                "61c0c5a71d1f82001bdaaa6d",
                "61c0c5a71d1f82001bdaaa6f"
        ));
        userClient.createOrder(order, accessToken);
    }

    @After
    public void tearDown() {
        if (accessToken != null) {
            userClient.deleteUser(accessToken);
        }
    }

    @Test
    @DisplayName("Получение заказов авторизованного пользователя")
    public void getUserOrdersWithAuthTest() {
        Response response = userClient.getUserOrders(accessToken);
        response.then()
                .statusCode(SC_OK)
                .body("success", equalTo(true))
                .body("orders", notNullValue())
                .body("orders", hasSize(greaterThan(0)));
    }

    @Test
    @DisplayName("Получение заказов неавторизованного пользователя")
    public void getUserOrdersWithoutAuthTest() {
        Response response = userClient.getUserOrders(null);
        response.then()
                .statusCode(SC_UNAUTHORIZED)
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"));
    }
}