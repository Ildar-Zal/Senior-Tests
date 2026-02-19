package iteration1;

import org.apache.http.HttpStatus;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;


import static io.restassured.RestAssured.given;

public class CreateAccoutTest extends BaseTest {

    @Test
    public void userCanCreateAccountTest() {
        String requestBody = String.format("""
                {
                  "username": "kate1990",
                  "password": "verysTRongPassword33$"
                }
                """);
       String token = given()
                .contentType("application/json")
                .body(requestBody)
                .when()
                .post("http://localhost:4111/api/v1/auth/login")
                .then()
                .extract()
                .header("authorization");

       given()
               .contentType("application/json")
               .header("Authorization", token)
               .when()
               .post("http://localhost:4111/api/v1/accounts")
               .then()
               .assertThat()
               .statusCode(HttpStatus.SC_CREATED);

       given()
               .contentType("application/json")
               .header("Authorization", token)
               .when()
               .get("http://localhost:4111/api/v1/customer/accounts")
               .then()
               .assertThat()
               .statusCode(HttpStatus.SC_OK)
               .body("accountNumber", Matchers.hasItem("ACC6"))
               .body("id",Matchers.hasItem(6));
    }
}
