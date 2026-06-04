package api.requests.skelethon.requesters;

import api.configs.Config;
import api.models.BaseModel;
import api.requests.skelethon.Endpoint;
import api.requests.skelethon.HttpRequest;
import api.requests.skelethon.interfaces.Crud;
import api.requests.skelethon.interfaces.GetAllEndpointInterface;
import common.helpers.StepLogger;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import static io.restassured.RestAssured.given;

public class CrudRequester extends HttpRequest implements Crud, GetAllEndpointInterface {

    private final static String API_VERSION = Config.getProperty("apiVersion");
    public CrudRequester(RequestSpecification requestSpecification, Endpoint endpoint, ResponseSpecification responseSpecification) {
        super(requestSpecification, endpoint, responseSpecification);
    }

    @Override
    public ValidatableResponse post(BaseModel model) {
       return StepLogger.log("POST request to " + endpoint.getUrl(), ()-> {
            var body = model == null ? "" : model;
            return given()
                    .spec(requestSpecification)
                    .body(body)
                    .when()
                    .post(API_VERSION+endpoint.getUrl())
                    .then()
                    .assertThat()
                    .spec(responseSpecification);
        });
    }

    @Override
    public ValidatableResponse get(Object... pathParams) {
        return StepLogger.log("GET request to " + endpoint.getUrl(), ()-> {
            return prepareRequest(pathParams)
                    .when()
                    .get()
                    .then()
                    .assertThat()
                    .spec(responseSpecification);
        });
    }

    @Override
    public ValidatableResponse put(BaseModel model, Object... pathParams) {
        return StepLogger.log("PUT request to " + endpoint.getUrl(), ()-> {
            var body = model == null ? "" : model;

            return prepareRequest(pathParams) // подготавливает урл и подставляет path параметры
                    .body(body)
                    .when()
                    .put() // путь уже сидит внутри request благодаря basePath
                    .then()
                    .assertThat()
                    .spec(responseSpecification);
        });
    }

    @Override
    public ValidatableResponse delete(Integer id) {
        return StepLogger.log("DELETE request to " + endpoint.getUrl(), ()-> {
            var url = id == null ? "" : "/" + id;
            return given()
                    .spec(requestSpecification)
                    .when()
                    .delete(endpoint.getUrl() + url)
                    .then()
                    .assertThat()
                    .spec(responseSpecification);
        });
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
                request.basePath(finalUrl);
            } else {
                request.basePath(finalUrl + "/" + params[0]);
            }
        } else {
            request.basePath(finalUrl);
        }

        return request;
    }

    @Override
    public ValidatableResponse getAll(Class<?> clazz) {
       return given()
                .spec(requestSpecification)
                .when()
                .get(endpoint.getUrl())
                .then()
                .spec(responseSpecification);
    }
}
