package iteration1.ui;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.Selenide;
import models.AccountResponse;
import models.CreateUserRequest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Alert;
import requests.skelethon.Endpoint;
import requests.skelethon.requesters.CrudRequester;
import requests.steps.AdminSteps;
import specs.RequestSpecs;
import specs.ResponseSpecs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.codeborne.selenide.Selenide.*;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

public class CreateAccountTest {
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
    public void userCanCreateAccountTest() {
        CreateUserRequest user = AdminSteps.createUser();

        String userAuthHeader = new CrudRequester(RequestSpecs.unauthSpec(), Endpoint.AUTH_LOGIN, ResponseSpecs.isOk())
                .post(CreateUserRequest.builder().username(user.getUsername()).password(user.getPassword()).build())
                .extract().header("Authorization");

        Selenide.open("/");

        executeJavaScript("localStorage.setItem('authToken', arguments[0]);", userAuthHeader);
        Selenide.open("/dashboard");

        $(Selectors.byText("➕ Create New Account")).click();

        Alert alert = switchTo().alert();

        assertThat(alert.getText()).contains("✅ New Account Created! Account Number:");

        alert.accept();

        Pattern pattern = Pattern.compile("Account Number: (\\w+)");

        Matcher matcher = pattern.matcher(alert.getText());
        matcher.find();

        String createdAccNumber = matcher.group(1);


        //Шаг 5: Аккаунт был создан на API

        AccountResponse[] existingUserAccount = given()
                .spec(RequestSpecs.userSpec(user.getUsername(),user.getPassword()))
                .get("http://localhost:4111/api/v1/customer/account")
                .then().assertThat()
                .extract().as(AccountResponse[].class);

        AccountResponse createdAccount = Arrays.stream(existingUserAccount)
                .filter(account -> account.getAccountNumber().equals(createdAccNumber)).findFirst().get();

        assertThat(createdAccount).isNotNull();
        assertThat(createdAccount.getBalance()).isZero();
        assertThat()
    }
}

