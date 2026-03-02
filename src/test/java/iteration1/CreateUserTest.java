package iteration1;

import models.LoginUserRequest;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import requests.LoginUserRequester;
import specs.RequestSpecs;
import specs.ResponseSpecs;

import java.util.stream.Stream;

import static io.restassured.RestAssured.given;


public class CreateUserTest extends BaseTest{

    @Test
    public void adminCanGenerateTokenTest() {
        LoginUserRequest loginUserRequest = LoginUserRequest.builder()
                .userName("admin")
                .password("admin")
                .build();
        new LoginUserRequester(RequestSpecs.unauthSpec(), ResponseSpecs.isOk())
                .post(loginUserRequest);
    }

    @Test
    public void adminCanCreateUserTest() {
       String username =  given()
                .accept("*/*")
                .contentType("application/json")
                .header("Authorization", "Basic YWRtaW46YWRtaW4=")
                .body("""
                        {
                          "username": "kate1990",
                          "password": "verysTRongPassword33$",
                          "role": "USER"
                        }
                        """)
                .when()
                .post("http://localhost:4111/api/v1/admin/users")
                .then()
                .statusCode(HttpStatus.SC_CREATED)
                .extract()
                .path("username");

        given()
                .contentType("application/json")
                .header("Authorization", "Basic YWRtaW46YWRtaW4=")
                .when()
                .get("http://localhost:4111/api/v1/admin/users")
                .then()
                .assertThat()
                .body("username",Matchers.hasItem(username));
    }

    public static Stream<Arguments> userInvalidData() {
        return Stream.of(
                Arguments.of("", "verysTRongPassword33$", "USER", "username", "Username cannot be blank"),
                Arguments.of("kate1991", "", "USER", "password", "Password cannot be blank"),
                Arguments.of("kate121231231311", "verysTRongPassword33$", "USER", "username", "Username must be between 3 and 15 characters"),
                Arguments.of("kate1991!", "verysTRongPassword33$", "USER", "username", "Username must contain only letters, digits, dashes, underscores, and dots"),
                Arguments.of("....", "verysTRongPassword33$", "USER", "username", "Username must contain only letters, digits, dashes, underscores, and dots")
        );
    }

    @MethodSource("userInvalidData")
    @ParameterizedTest(name = "Негативные тесты")
    public void adminCanCreateUserTest(String username, String password, String role, String errorKey, String errorValue) {
        String requestBody = String.format("""
                {
                  "username": "%s",
                  "password": "%s",
                  "role": "%s"
                }
                """, username, password, role);
        given()
                .accept("*/*")
                .contentType("application/json")
                .header("Authorization", "Basic YWRtaW46YWRtaW4=")
                .body(requestBody)
                .when()
                .post("http://localhost:4111/api/v1/admin/users")
                .then()
                .assertThat()
                .body(errorKey, Matchers.hasItem(errorValue));
    }
}

