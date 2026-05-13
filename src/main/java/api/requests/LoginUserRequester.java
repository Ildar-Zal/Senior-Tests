package api.requests;

import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import api.models.LoginUserRequest;

import static io.restassured.RestAssured.given;

public class LoginUserRequester extends Request<LoginUserRequest> {

    public LoginUserRequester(RequestSpecification requestSpecification, ResponseSpecification responseSpecification) {
        super(requestSpecification, responseSpecification);
    }

    @Override
    public ValidatableResponse post(LoginUserRequest body) {
        return given()
                .spec(requestSpecification)
                .body(body)
                .when()
                .post("/api/v1/auth/login")
                .then()
                .spec(responseSpecification);
    }
}
