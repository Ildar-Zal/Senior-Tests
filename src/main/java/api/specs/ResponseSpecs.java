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

import java.util.Collection;

import static org.hamcrest.Matchers.*;

public class ResponseSpecs {
    private ResponseSpecs() {
    }

    private static ResponseSpecBuilder defaultSpec() {
        return new ResponseSpecBuilder();
    }

    public static ResponseSpecification anyStatus() {
        // Просто собираем дефолтную спецификацию, НЕ накладывая ограничений на expectStatusCode
        return defaultSpec().build();
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
                .expectBody(errorKey, equalTo(errorMessage))
                .build();
    }

    public static ResponseSpecification isForbidden() {
        var spec = defaultSpec().expectStatusCode(HttpStatus.SC_FORBIDDEN);
        return spec.expectBody("message",equalTo("Unauthorized access to account")).build();
    }

    public static ResponseSpecification isBadRequest(String errorKey, Object expectedValue) {
        var spec = defaultSpec().expectStatusCode(HttpStatus.SC_BAD_REQUEST);

        if (errorKey == null || errorKey.isEmpty()) {
            return spec.expectBody(equalTo(expectedValue)).build();
        }

        // Умное решение: говорим RestAssured, что нам подойдет ЛИБО точное совпадение строки,
        // ЛИБО если эта строка является элементом массива.
        return spec.expectBody(errorKey, anyOf(equalTo(expectedValue), hasItem(expectedValue))).build();
    }
}
