package ui.iteration1;

import common.SessionStorage.SessionStorage;
import common.annotations.UserSession;
import org.junit.jupiter.api.Test;
import ui.BaseUiTest;
import ui.pages.BankAlert;
import ui.pages.UserDashboard;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

public class CreateAccountTest extends BaseUiTest {

    @Test
    @UserSession
    public void userCanCreateAccountTest() {
//        CreateUserRequest user = AdminSteps.createUser();
//        authAsUser(user);



        new UserDashboard().open().createNewAccount();
        var accounts = SessionStorage.getSteps().getAccounts();

        new UserDashboard().checkAlertMessageAndAccept(BankAlert.NEW_ACCOUNT_CREATED.getMessage() + accounts.getFirst().getAccountNumber());
        assertThat(accounts.getFirst().getBalance()).isZero();
//        Alert alert = switchTo().alert();
//
//        assertThat(alert.getText()).contains("New Account Created! Account Number:");
//        String alletgettext = alert.getText();
//        alert.accept();

//        Pattern pattern = Pattern.compile("Account Number (\\w+)");
//        Matcher matcher = pattern.matcher(alletgettext);
//
//        matcher.find();
//
//        String createdAccNumber = matcher.group(1);
//
//        var accounts = given()
//                .spec(RequestSpecs.userSpec(user.getUsername(), user.getPassword()))
//                .when()
//                .get("/api/v1/customer/accounts")
//                .then()
//                .spec(ResponseSpecs.isOk())
//                .extract()
//                .jsonPath()
//                .getList("", AccountResponse.class);
//
//        var account = accounts.stream().filter(a->a.getAccountNumber().equals(createdAccNumber)).findFirst().get();
//
//        assertThat(account.getBalance()).isZero();
//        assertThat(account).isNotNull();



//        CreateUserRequest user = AdminSteps.createUser();
//
//        String userAuthHeader = new CrudRequester(RequestSpecs.unauthSpec(), Endpoint.AUTH_LOGIN, ResponseSpecs.isOk())
//                .post(CreateUserRequest.builder().username(user.getUsername()).password(user.getPassword()).build())
//                .extract().header("Authorization");
//
//        Selenide.open("/");
//
//        executeJavaScript("localStorage.setItem('authToken', arguments[0]);", userAuthHeader);
//        Selenide.open("/dashboard");
//
//        $(byText("➕ Create New Account")).click();
//
//        Alert alert = switchTo().alert();
//
//        assertThat(alert.getText()).contains("✅ New Account Created! Account Number:");
//
//        alert.accept();
//
//        Pattern pattern = Pattern.compile("Account Number: (\\w+)");
//
//        Matcher matcher = pattern.matcher(alert.getText());
//        matcher.find();
//
//        String createdAccNumber = matcher.group(1);
//
//
//        //Шаг 5: Аккаунт был создан на API
//
//        AccountResponse[] existingUserAccount = given()
//                .spec(RequestSpecs.userSpec(user.getUsername(), user.getPassword()))
//                .get("http://localhost:4111/api/v1/customer/account")
//                .then().assertThat()
//                .extract().as(AccountResponse[].class);
//
//        AccountResponse createdAccount = Arrays.stream(existingUserAccount)
//                .filter(account -> account.getAccountNumber().equals(createdAccNumber)).findFirst().get();
//
//        assertThat(createdAccount).isNotNull();
//        assertThat(createdAccount.getBalance()).isZero();
//        assertThat()
    }
}

