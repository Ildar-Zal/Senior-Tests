package ui.iteration2;

import api.generators.RandomModelGenerator;
import api.models.DepositAccountRequest;
import common.context.SessionStorage;
import common.annotations.UserSession;
import common.utils.ApiWait;
import org.junit.jupiter.api.Test;
import ui.BaseUiTest;
import ui.pages.BankAlert;
import ui.pages.DepositPage;
import ui.pages.UserDashboard;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

public class DepositAccountTest extends BaseUiTest {

    @UserSession
    @Test
    public void userCanDepositAccountTest() {
        var user = SessionStorage.getSteps();
        var createdAccount = user.createAccount();
        BigDecimal depositAmount = RandomModelGenerator.generate(DepositAccountRequest.class).getBalance().stripTrailingZeros();

        new UserDashboard().open().openDepositPage().depositMoney(createdAccount.getAccountNumber(), depositAmount.toString());
        var balance = ApiWait.untilNotNull(() -> SessionStorage.getSteps().getAccounts().getFirst().getBalance());
        new UserDashboard().checkAlertMessageAndAccept(BankAlert.SUCCESSFULLY_DEPOSITED,
                balance, createdAccount.getAccountNumber());


        assertThat(balance).isEqualByComparingTo(depositAmount);
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
