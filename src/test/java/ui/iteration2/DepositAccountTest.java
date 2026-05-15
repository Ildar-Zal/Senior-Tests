package ui.iteration2;

import api.generators.RandomModelGenerator;
import api.models.DepositAccountRequest;
import common.SessionStorage.SessionStorage;
import common.annotations.UserSession;
import org.junit.jupiter.api.Test;
import ui.BaseUiTest;
import ui.pages.BankAlert;
import ui.pages.DepositPage;
import ui.pages.UserDashboard;

import static org.assertj.core.api.Assertions.assertThat;

public class DepositAccountTest extends BaseUiTest {

    @UserSession
    @Test
    public void userCanDepositAccountTest() {
        var user = SessionStorage.getSteps();
        var createdAccount = user.createAccount();
        var depositAmount = RandomModelGenerator.generate(DepositAccountRequest.class).getBalance();

        new UserDashboard().open().openDepositPage().depositMoney(createdAccount.getAccountNumber(), depositAmount.toString());
        var account = SessionStorage.getSteps().getAccount(createdAccount.getId());
        new UserDashboard().checkAlertMessageAndAccept(BankAlert.SUCCESSFULLY_DEPOSITED,
                account.getBalance(), createdAccount.getAccountNumber());

        assertThat(account.getBalance()).isEqualTo(depositAmount);
    }

    @UserSession
    @Test
    public void userCantDepositAccountWithoutAmountTest() {
        var user = SessionStorage.getSteps();
        var createdAccount = user.createAccount();

        new DepositPage().open().selectAccount(createdAccount.getAccountNumber()).clickDeposit()
                .checkAlertMessageAndAccept(BankAlert.PLEASE_ENTER_AMOUNT.getMessage());
        var account = SessionStorage.getSteps().getAccount(createdAccount.getId());

        assertThat(account.getBalance()).isZero();
    }

    @UserSession
    @Test
    public void userCantDepositAccountWithoutAccountTest() {
        var user = SessionStorage.getSteps();
        var createdAccount = user.createAccount();

        new DepositPage().open().clickDeposit()
                .checkAlertMessageAndAccept(BankAlert.PLEASE_SELECT_ACCOUNT.getMessage());
        var account = SessionStorage.getSteps().getAccount(createdAccount.getId());

        assertThat(account.getBalance()).isZero();
    }
}
