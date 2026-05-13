package api.specs;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.protocol.HTTP;
import org.hamcrest.Matchers;

public class ResponseSpecs {
    private ResponseSpecs(){}

    private static ResponseSpecBuilder defaultSpec() {
        return new ResponseSpecBuilder();
    }

    public static ResponseSpecification isOk() {
        return defaultSpec().expectStatusCode(HttpStatus.SC_OK)
                .build();
    }

    public static ResponseSpecification isCreated() {
        return defaultSpec().expectStatusCode(HttpStatus.SC_CREATED)
                .build();
    }

    public static ResponseSpecification isUnathorized(String errorKey, String errorMessage) {
        return defaultSpec().expectStatusCode(HttpStatus.SC_UNAUTHORIZED)
                .expectBody(errorKey, Matchers.equalTo(errorMessage))
                .build();
    }

    public static ResponseSpecification isForbidden(String errorKey, String errorMessage) {
        var spec = defaultSpec().expectStatusCode(HttpStatus.SC_FORBIDDEN);

        if (errorKey == null || errorKey.isEmpty()) {
            return spec.expectBody(Matchers.equalTo(errorMessage)).build();
        } else {
            return spec.expectBody(errorKey, Matchers.hasItem(errorMessage)).build();
        }
    }

    public static ResponseSpecification isBadRequest(String errorKey, String errorMessage) {
        var spec = defaultSpec().expectStatusCode(HttpStatus.SC_BAD_REQUEST);

        if (errorKey == null || errorKey.isEmpty()) {
            return spec.expectBody(Matchers.equalTo(errorMessage)).build();
        } else {
            return spec.expectBody(errorKey, Matchers.hasItem(errorMessage)).build();
        }
    }
}
