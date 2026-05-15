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
        new UserDashboard().open().createNewAccount();
        var accounts = SessionStorage.getSteps().getAccounts();

        new UserDashboard().checkAlertMessageAndAccept(BankAlert.NEW_ACCOUNT_CREATED.getMessage() + accounts.getFirst().getAccountNumber());
        assertThat(accounts.getFirst().getBalance()).isZero();

    }
}

