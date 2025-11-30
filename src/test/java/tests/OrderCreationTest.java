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
import java.util.Collections;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.*;

public class OrderCreationTest {
    private UserClient userClient;
    private User user;
    private String accessToken;

    @Before
    public void setUp() {
        userClient = new UserClient();
        user = DataGenerator.getRandomUser();

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
    @DisplayName("Создание заказа с авторизацией и ингредиентами")
    public void createOrderWithAuthAndIngredientsTest() {
        Order order = new Order(Arrays.asList(
                "61c0c5a71d1f82001bdaaa6d",
                "61c0c5a71d1f82001bdaaa6f"
        ));
        Response response = userClient.createOrder(order, accessToken);
        response.then()
                .statusCode(SC_OK)
                .body("success", equalTo(true))
                .body("order.number", notNullValue());
    }

    @Test
    @DisplayName("Создание заказа без авторизации с ингредиентами")
    public void createOrderWithoutAuthWithIngredientsTest() {
        Order order = new Order(Arrays.asList(
                "61c0c5a71d1f82001bdaaa6d",
                "61c0c5a71d1f82001bdaaa6f"
        ));
        Response response = userClient.createOrder(order, null);
        response.then()
                .statusCode(SC_OK)
                .body("success", equalTo(true))
                .body("order.number", notNullValue());
    }

    @Test
    @DisplayName("Создание заказа без ингредиентов")
    public void createOrderWithoutIngredientsTest() {
        Order order = new Order(Collections.emptyList());
        Response response = userClient.createOrder(order, accessToken);
        response.then()
                .statusCode(SC_BAD_REQUEST)
                .body("success", equalTo(false))
                .body("message", equalTo("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Создание заказа с неверным хешем ингредиентов")
    public void createOrderWithInvalidIngredientHashTest() {
        Order order = new Order(Arrays.asList("invalid_hash_1", "invalid_hash_2"));
        Response response = userClient.createOrder(order, accessToken);
        response.then()
                .statusCode(SC_INTERNAL_SERVER_ERROR);
    }

    @Test
    @DisplayName("Создание заказа без авторизации и без ингредиентов")
    public void createOrderWithoutAuthAndWithoutIngredientsTest() {
        Order order = new Order(Collections.emptyList());
        Response response = userClient.createOrder(order, null);

        response.then()
                .statusCode(SC_BAD_REQUEST)
                .body("success", equalTo(false));
    }

    @Test
    @DisplayName("Создание заказа без авторизации с неверным хешем ингредиентов")
    public void createOrderWithoutAuthWithInvalidIngredientHashTest() {
        Order order = new Order(Arrays.asList("invalid_hash_1", "invalid_hash_2"));
        Response response = userClient.createOrder(order, null);

        response.then()
                .statusCode(SC_INTERNAL_SERVER_ERROR);
    }
}