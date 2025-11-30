package clients;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import models.User;

import static io.restassured.RestAssured.given;

public class UserClient {

    private static final String BASE_URL = "https://stellarburgers.education-services.ru/api";

    @Step("Создание пользователя")
    public Response createUser(User user) {
        return given()
                .header("Content-type", "application/json")
                .baseUri(BASE_URL)
                .body(user)
                .when()
                .post("/auth/register");
    }

    @Step("Логин пользователя")
    public Response loginUser(User user) {
        return given()
                .header("Content-type", "application/json")
                .baseUri(BASE_URL)
                .body(user)
                .when()
                .post("/auth/login");
    }

    @Step("Удаление пользователя")
    public Response deleteUser(String accessToken) {
        return given()
                .header("Authorization", accessToken)
                .baseUri(BASE_URL)
                .when()
                .delete("/auth/user");
    }

    @Step("Обновление данных пользователя")
    public Response updateUser(User user, String accessToken) {
        return given()
                .header("Content-type", "application/json")
                .header("Authorization", accessToken != null ? accessToken : "")
                .baseUri(BASE_URL)
                .body(user)
                .when()
                .patch("/auth/user");
    }

    @Step("Создание заказа")
    public Response createOrder(Object order, String accessToken) {
        if (accessToken != null) {
            return given()
                    .header("Content-type", "application/json")
                    .header("Authorization", accessToken)
                    .baseUri(BASE_URL)
                    .body(order)
                    .when()
                    .post("/orders");
        } else {
            return given()
                    .header("Content-type", "application/json")
                    .baseUri(BASE_URL)
                    .body(order)
                    .when()
                    .post("/orders");
        }
    }

    @Step("Получение заказов пользователя")
    public Response getUserOrders(String accessToken) {
        if (accessToken != null) {
            return given()
                    .header("Authorization", accessToken)
                    .baseUri(BASE_URL)
                    .when()
                    .get("/orders");
        } else {
            return given()
                    .baseUri(BASE_URL)
                    .when()
                    .get("/orders");
        }
    }
}