package requests.skelethon.requesters;

import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import models.BaseModel;
import requests.skelethon.Endpoint;
import requests.skelethon.HttpRequest;
import requests.skelethon.interfaces.Crud;

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
    public ValidatableResponse get(Integer id) {
        var url = id == null ? "" : "/" + id;
        return given()
                .spec(requestSpecification)
                .when()
                .get(endpoint.getUrl() + url)
                .then()
                .assertThat()
                .spec(responseSpecification);
    }

    @Override
    public ValidatableResponse put(Integer id, BaseModel model) {
        var url = id == null ? "" : "/" + id;
        var body = model == null ? "" : model;
        return given()
                .spec(requestSpecification)
                .body(body)
                .when()
                .put(endpoint.getUrl() + url)
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
}
