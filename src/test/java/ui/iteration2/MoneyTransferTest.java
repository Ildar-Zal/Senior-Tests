package ui.iteration2;

import api.models.enums.TransactionType;
import common.SessionStorage.SessionStorage;
import common.annotations.AccountSession;
import common.annotations.UserSession;
import org.junit.jupiter.api.Test;
import ui.BaseUiTest;
import ui.pages.BankAlert;
import ui.pages.TransactionInfoPage;
import ui.pages.TransferPage;
import ui.pages.UserDashboard;

import java.math.BigDecimal;

public class MoneyTransferTest extends BaseUiTest {

    @UserSession(2)
    @AccountSession(2)
    @Test
    public void userCanTransferMoneyTest() {
        var user1 = SessionStorage.getSteps(1);
        var user2 = SessionStorage.getSteps(2);
        var account1 = SessionStorage.getAccount(1);
        var account2 = SessionStorage.getAccount(2);
        var username1 = user1.getUserProfile().getUsername();
        var username2 = user2.getUserProfile().getUsername();


        new UserDashboard().open().openTransferPage().sendTransfer(account1.getAccountNumber(), "",
                        account2.getAccountNumber(), account1.getBalance().toString()).
                checkAlertMessageAndAccept(BankAlert.SUCCESSFULLY_TRANSFERED, account1.getBalance(),
                        account2.getAccountNumber());

        var allTransactionUser1FromUi = new TransferPage().openTrasactionInfoPage().searchTransactions(username1).getAllTransactions();
        var allTransactionUser2FromUi = new TransactionInfoPage().searchTransactions(username2).getAllTransactions();

        var allTransactionUser1FromApi = user1.getTransaction(account1.getId());
        var allTransactionUser2FromApi = user2.getTransaction(account2.getId());

        allTransactionUser1FromUi.stream().forEach(uiTx -> {
                    var transactionApi = allTransactionUser1FromApi.stream().
                            filter(apiTx -> apiTx.getType().toString().equals(uiTx.getType()))
                            .findFirst().
                            orElseThrow(() -> new AssertionError("Транзакция с типом " + uiTx.getType() + " не найдена в API"));

                    softly.assertThat(uiTx.getType()).as("Тип транзакции у User1")
                            .isEqualTo(transactionApi.getType().toString());

                    softly.assertThat(new BigDecimal(uiTx.getAmount())).as("Сумма транзакции у User1")
                            .isEqualByComparingTo(new BigDecimal(transactionApi.getAmount().toString()));
                }
        );
        softly.assertThat(allTransactionUser1FromUi).as("Количество транзакций")
                .hasSameSizeAs(allTransactionUser1FromApi);

        softly.assertThat(allTransactionUser2FromUi).as("Количество транзакций API и UI")
                .hasSameSizeAs(allTransactionUser2FromApi);

        softly.assertThat(allTransactionUser2FromUi).as("Количество транзакций должна быть 1")
                .hasSize(1);

        softly.assertThat(allTransactionUser2FromUi.getFirst().getType())
                .as("Тип транзакции у User2")
                .isEqualTo(allTransactionUser2FromApi.getFirst().getType().toString());

        softly.assertThat(new BigDecimal(allTransactionUser2FromUi.getFirst().getAmount()))
                .as("Сумма транзакции у User2")
                .isEqualByComparingTo(new BigDecimal(allTransactionUser2FromApi.getFirst().getAmount().toString()));

    }

    @UserSession
    @AccountSession
    @Test
    public void userCantTransferMoneyWithEmptyFieldTest() {
        var user1 = SessionStorage.getSteps(1);
        var account1 = SessionStorage.getAccount();
        var username1 = user1.getUserProfile().getUsername();

        new UserDashboard().open().openTransferPage().clickTrasfetButton().
                checkAlertMessageAndAccept(BankAlert.PLEASE_FILL_ALL_FILED.getMessage());

        var allTransactionUser1FromUi = new TransferPage().openTrasactionInfoPage().searchTransactions(username1).getAllTransactions();

        var balance = user1.getAccount(account1.getId()).getBalance();
        var transaction = user1.getTransaction(account1.getId());

        softly.assertThat(account1.getBalance()).isEqualTo(balance);

        softly.assertThat(transaction).hasSize(1);

        softly.assertThat(transaction.getFirst().getType()).isEqualTo(TransactionType.DEPOSIT);

        softly.assertThat(allTransactionUser1FromUi).hasSize(1);

        softly.assertThat(allTransactionUser1FromUi.getFirst().getType())
                .isEqualTo(TransactionType.DEPOSIT.toString());

        softly.assertThat(new BigDecimal(allTransactionUser1FromUi.getFirst().getAmount()))
                .isEqualByComparingTo(new BigDecimal(balance.toString()));


    }

    @UserSession()
    @AccountSession()
    @Test
    public void userNotFoundWithAccountTest() {
        var account1 = SessionStorage.getAccount();

        new UserDashboard().open().openTransferPage().sendTransfer(account1.getAccountNumber(), "",
                        "123", account1.getBalance().toString()).
                checkAlertMessageAndAccept(BankAlert.NO_USER_FOUNT_WITH_ACCOUNT.getMessage());
    }

    @UserSession(2)
    @AccountSession(2)
    @Test
    public void userCantTransferMoneyTest() {
        var user1 = SessionStorage.getSteps(1);
        var user2 = SessionStorage.getSteps(2);
        var account1 = SessionStorage.getAccount(1);
        var account2 = SessionStorage.getAccount(2);
        var username1 = user1.getUserProfile().getUsername();
        var username2 = user2.getUserProfile().getUsername();

        new UserDashboard().open().openTransferPage().sendTransfer(account1.getAccountNumber(), "",
                        account2.getAccountNumber(), "5001").
                checkAlertMessageAndAccept(BankAlert.ERROR_INVALID_TRASFER);

        var allTransactionUser1FromUi = new TransferPage().openTrasactionInfoPage().searchTransactions(username1).getAllTransactions();
        var allTransactionUser2FromUi = new TransactionInfoPage().searchTransactions(username2).getAllTransactions();

        softly.assertThat(allTransactionUser1FromUi).as("Количество транзакций")
                .hasSize(1);
        softly.assertThat(allTransactionUser2FromUi).as("Количество транзакций")
                .hasSize(0);

        softly.assertThat(allTransactionUser1FromUi)
                .as("У отправителя не должно быть транзакций перевода")
                .extracting("type")
                .doesNotContain(TransactionType.TRANSFER_OUT.toString());

        softly.assertThat(user1.getAccount(account1.getId()).getBalance())
                .as("Баланс в API не изменился")
                .isEqualByComparingTo(account1.getBalance());

        softly.assertThat(user1.getTransaction(account1.getId()))
                .as("Новых транзакций в API не появилось")
                .hasSize(1);
    }
}
