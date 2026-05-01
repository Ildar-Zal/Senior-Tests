package iteration1.ui;

import com.codeborne.selenide.*;
import models.CreateUserRequest;
import models.UserResponse;
import models.comparison.ModelAssertions;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Alert;
import requests.steps.AdminSteps;
import specs.RequestSpecs;

import java.util.Arrays;
import java.util.Map;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.switchTo;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CreateUserTest {


    @BeforeAll
    public static void setupSelenoid() {
        Configuration.remote = "http://localhost:4444/wd/hub";
        Configuration.baseUrl = "http://172.30.0.1:3000";
        Configuration.browser = "chrome";
        Configuration.browserSize = "1920x1080";

        Configuration.browserCapabilities.setCapability("selenoid:options",
                Map.of("enableVNC", true, "enableLog", true)
        );
    }

    @Test
    public void adminCanCreateUserTest() {
        CreateUserRequest admin = CreateUserRequest.builder().username("admin").password("admin").build();

        Selenide.open("/login");
        $(Selectors.byAttribute("placeholder", "Username")).sendKeys(admin.getUsername());
        $(Selectors.byAttribute("placeholder", "Password")).sendKeys(admin.getPassword());
        $("button").click();

        $(Selectors.byText("Admin Panel")).shouldBe(Condition.visible);

        CreateUserRequest newUser = AdminSteps.createUser();

        $(Selectors.byAttribute("placeholder", "Username")).sendKeys(newUser.getUsername());
        $(Selectors.byAttribute("placeholder", "Password")).sendKeys(newUser.getPassword());

        $(Selectors.byText("Add User")).click();

        Alert alert = switchTo().alert();

        assertEquals(alert.getText(), "✅ User created successfully!");

        alert.accept();

        //Шаг 4: проверка, что юзер отображается на UI

        ElementsCollection allUsersFromDashboard = $(Selectors.byText("All Users")).parent().findAll("li");
        allUsersFromDashboard.findBy(Condition.exactText(newUser.getUsername() + "\nUSER")).shouldBe(Condition.visible);

        UserResponse[] users = given()
                .spec(RequestSpecs.adminSpec())
                .get("http://localhost:4111/v1/admin/users")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .extract().as(UserResponse[].class);

        UserResponse createdUser = Arrays.stream(users).filter(user1 -> user1.getUsername().equals(newUser.getUsername())).findFirst().get();

        ModelAssertions.assertThatModels(createdUser, newUser).match();
        //Шаг 5: проверка, что юзер отображается на API

    }

    @Test
    public void adminCannotCreateUserWithInvalidData() {
        CreateUserRequest admin = CreateUserRequest.builder().username("admin").password("admin").build();

        Selenide.open("/login");
        $(Selectors.byAttribute("placeholder", "Username")).sendKeys(admin.getUsername());
        $(Selectors.byAttribute("placeholder", "Password")).sendKeys(admin.getPassword());
        $("button").click();

        $(Selectors.byText("Admin Panel")).shouldBe(Condition.visible);

        CreateUserRequest newUser = AdminSteps.createUser();
        newUser.setUsername("a");

        $(Selectors.byAttribute("placeholder", "Username")).sendKeys(newUser.getUsername());
        $(Selectors.byAttribute("placeholder", "Password")).sendKeys(newUser.getPassword());

        $(Selectors.byText("Add User")).click();

        Alert alert = switchTo().alert();

        assertThat(alert.getText()).contains("Username must be between 3 and 15 characters");

        alert.accept();

        //Шаг 4: проверка, что юзер отображается на UI

        ElementsCollection allUsersFromDashboard = $(Selectors.byText("All Users")).parent().findAll("li");
        allUsersFromDashboard.findBy(Condition.exactText(newUser.getUsername() + "\nUSER")).shouldNot(Condition.exist);

        UserResponse[] users = given()
                .spec(RequestSpecs.adminSpec())
                .get("http://localhost:4111/v1/admin/users")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .extract().as(UserResponse[].class);

        long usersWithSameUserNameAsNewUser = Arrays.stream(users).filter(user1 -> user1.getUsername().equals(newUser.getUsername())).count();
        assertThat(usersWithSameUserNameAsNewUser).isZero();

        //Шаг 5: проверка, что юзер отображается на API
    }
}
