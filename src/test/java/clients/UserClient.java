package clients;

import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import models.User;

import static io.restassured.RestAssured.given;

public class UserClient {

    private static final String BASE_URL = "https://stellarburgers.education-services.ru/api";

    private RequestSpecification getBaseSpec() {
        return given()
                .baseUri(BASE_URL)
                .contentType(ContentType.JSON);
    }

    private RequestSpecification getAuthSpec(String accessToken) {
        return getBaseSpec()
                .header("Authorization", accessToken);
    }

    @Step("Создание пользователя")
    public Response createUser(User user) {
        return getBaseSpec()
                .body(user)
                .when()
                .post("/auth/register");
    }

    @Step("Логин пользователя")
    public Response loginUser(User user) {
        return getBaseSpec()
                .body(user)
                .when()
                .post("/auth/login");
    }

    @Step("Удаление пользователя")
    public Response deleteUser(String accessToken) {
        return getAuthSpec(accessToken)
                .when()
                .delete("/auth/user");
    }

    @Step("Обновление данных пользователя")
    public Response updateUser(User user, String accessToken) {
        if (accessToken != null && !accessToken.isEmpty()) {
            return getAuthSpec(accessToken)
                    .body(user)
                    .when()
                    .patch("/auth/user");
        } else {
            return getBaseSpec()
                    .body(user)
                    .when()
                    .patch("/auth/user");
        }
    }

    @Step("Создание заказа")
    public Response createOrder(Object order, String accessToken) {
        if (accessToken != null && !accessToken.isEmpty()) {
            return getAuthSpec(accessToken)
                    .body(order)
                    .when()
                    .post("/orders");
        } else {
            return getBaseSpec()
                    .body(order)
                    .when()
                    .post("/orders");
        }
    }

    @Step("Получение заказов пользователя")
    public Response getUserOrders(String accessToken) {
        if (accessToken != null && !accessToken.isEmpty()) {
            return getAuthSpec(accessToken)
                    .when()
                    .get("/orders");
        } else {
            return getBaseSpec()
                    .when()
                    .get("/orders");
        }
    }

    @Step("Получение списка ингредиентов")
    public Response getIngredients() {
        return getBaseSpec()
                .when()
                .get("/ingredients");
    }
}