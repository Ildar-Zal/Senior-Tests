package iteration1;

import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static io.restassured.RestAssured.given;

public class LoginUserTest extends BaseTest{

    @Test
    public void userCanLoginTest() {
        String requestBody = String.format("""
                {
                  "username": "kate1990",
                  "password": "verysTRongPassword33$"
                }
                """);
        given()
                .accept("*/*")
                .contentType("application/json")
                .body(requestBody)
                .when()
                .post("http://localhost:4111/api/v1/auth/login")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK);
    }

    public static Stream<Arguments> invalidUserData() {
        return Stream.of(
                Arguments.of("", "verysTRongPassword33$", "error", "Invalid username or password"),
                Arguments.of("kate1991", "", "error", "Invalid username or password"),
                Arguments.of("kate121231231311", "verysTRongPassword33$", "error", "Invalid username or password"),
                Arguments.of("kate1991!", "verysTRongPassword33$", "error", "Invalid username or password"),
                Arguments.of("....", "verysTRongPassword33$", "username", "....")
        );
    }

    @MethodSource("invalidUserData")
    @ParameterizedTest(name = "Негативные тесты")
    public void userCantLoginTest(String userName, String password, String errorKey, String errorValue) {
        String requestBody = String.format("""
                {
                  "username": "%s",
                  "password": "%s"
                }
                """, userName, password);
        given()
                .accept("*/*")
                .contentType("application/json")
                .body(requestBody)
                .when()
                .post("http://localhost:4111/api/v1/auth/login")
                .then()
                .assertThat()
                .body(errorKey, Matchers.equalTo(errorValue));
    }
}
