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
        var accountNumber = user.createAccount().getAccountNumber();
        var depositAmount = RandomModelGenerator.generate(DepositAccountRequest.class).getBalance();

        new UserDashboard().open().openDepositPage().depositMoney(accountNumber, depositAmount.toString());
        var account = SessionStorage.getSteps().getAccount(accountNumber);
        new UserDashboard().checkAlertMessageAndAccept(BankAlert.SUCCESSFULLY_DEPOSITED,
                account.getBalance().toString(), accountNumber);

        assertThat(account.getBalance()).isEqualTo(depositAmount);

    }

    @UserSession
    @Test
    public void userCantDepositAccountWithoutAmountTest() {
        var user = SessionStorage.getSteps();
        var accountNumber = user.createAccount().getAccountNumber();

        new DepositPage().selectAccount(accountNumber).clickDeposit()
                .checkAlertMessageAndAccept(BankAlert.PLEASE_ENTER_AMOUNT.getMessage());
        var account = SessionStorage.getSteps().getAccount(accountNumber);

        assertThat(account.getBalance()).isZero();
    }

    @UserSession
    @Test
    public void userCantDepositAccountWithoutAccountTest() {
        var user = SessionStorage.getSteps();
        var accountNumber = user.createAccount().getAccountNumber();

        new DepositPage().selectAccount(accountNumber).clickDeposit()
                .checkAlertMessageAndAccept(BankAlert.PLEASE_SELECT_ACCOUNT.getMessage());
        var account = SessionStorage.getSteps().getAccount(accountNumber);

        assertThat(account.getBalance()).isZero();
    }
}
