package requests.skelethon.requesters;

import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import models.BaseModel;
import requests.skelethon.Endpoint;
import requests.skelethon.HttpRequest;
import requests.skelethon.interfaces.Crud;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import static io.restassured.RestAssured.given;

public class CrudRequester extends HttpRequest implements Crud {

    public CrudRequester(RequestSpecification requestSpecification, Endpoint endpoint, ResponseSpecification responseSpecification) {
        super(requestSpecification, endpoint, responseSpecification);
    }

    @Override
    public ValidatableResponse post(BaseModel model) {
        var body = model == null ? "" : model;
        return given()
                .spec(requestSpecification)
                .body(body)
                .when()
                .post(endpoint.getUrl())
                .then()
                .assertThat()
                .spec(responseSpecification);
    }

    @Override
    public ValidatableResponse get(Object... pathParams) {
        return prepareRequest(pathParams)
                .when()
                .get()
                .then()
                .assertThat()
                .spec(responseSpecification);
    }

    @Override
    public ValidatableResponse put(BaseModel model, Object... pathParams) {
        var body = model == null ? "" : model;

        return prepareRequest(pathParams) // подготавливает урл и подставляет path параметры
                .body(body)
                .when()
                .put() // путь уже сидит внутри request благодаря basePath
                .then()
                .assertThat()
                .spec(responseSpecification);
    }

    @Override
    public ValidatableResponse delete(Integer id) {
        var url = id == null ? "" : "/" + id;
        return given()
                .spec(requestSpecification)
                .when()
                .delete(endpoint.getUrl() + url)
                .then()
                .assertThat()
                .spec(responseSpecification);
    }

    private RequestSpecification prepareRequest(Object... params) {
        var request = given().spec(requestSpecification);
        String finalUrl = endpoint.getUrl();

        if (params != null && params.length > 0) {
            if (endpoint.isDynamic()) {
                Map<String, Object> map = new HashMap<>();
                var matcher = Pattern.compile("\\{([^}]+)\\}").matcher(finalUrl);
                int i = 0;
                while (matcher.find() && i < params.length) {
                    map.put(matcher.group(1), params[i]);
                    i++;
                }
                request.pathParams(map);
                request.basePath(finalUrl); // Устанавливаем базу для динамического урла
            } else {
                // Для старых эндпоинтов типа /customer/profile/123
                request.basePath(finalUrl + "/" + params[0]);
            }
        } else {
            // Если параметров вообще нет (например, PUT на /customer/profile)
            request.basePath(finalUrl);
        }

        return request;
    }
}
