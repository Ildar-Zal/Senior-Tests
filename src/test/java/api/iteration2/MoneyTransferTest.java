package api.iteration2;

import api.dao.comparison.DaoAndModelAssertions;
import api.models.CreateUserRequest;
import api.models.TransactionResponse;
import api.models.TransferAccountRequest;
import api.models.enums.TransactionType;
import api.requests.steps.DataBaseSteps;
import base.BaseTest;
import api.models.comparison.ModelAssertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import api.requests.skelethon.Endpoint;
import api.requests.skelethon.requesters.CrudRequester;
import api.requests.steps.AdminSteps;
import api.requests.steps.UserSteps;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Stream;


public class MoneyTransferTest extends BaseTest {

    @Test
    public void userCanTransferMoneyDifferentUserTest() {
        CreateUserRequest userRequest1 = AdminSteps.createUser();
        CreateUserRequest userRequest2 = AdminSteps.createUser();
        var sender = new UserSteps(userRequest1);
        var receiver = new UserSteps(userRequest2);
        var senderAcc = sender.createAccount();
        var receiverAcc = receiver.createAccount();

        var depositedAccount = sender.depositRandomMoneyToAccount(senderAcc);
        var transferAmount = depositedAccount.getBalance();

        TransferAccountRequest transferRequest = TransferAccountRequest.builder()
                .senderAccountId(senderAcc.getId())
                .receiverAccountId(receiverAcc.getId())
                .amount(transferAmount)
                .build();

        new CrudRequester(
                RequestSpecs.userSpec(userRequest1.getUsername(), userRequest1.getPassword()),
                Endpoint.ACCOUNTS_TRANSFER,
                ResponseSpecs.isOk())
                .post(transferRequest);


        var senderAccAfter = sender.getAccount(senderAcc.getId());
        var receiverAccAfter = receiver.getAccount(receiverAcc.getId());

        var senderTransactions = sender.getTransaction(senderAcc.getId());
        var receiverTransactions = receiver.getTransaction(receiverAcc.getId());


        softly.assertThat(senderAccAfter.getBalance())
                .as("Баланс отправителя после перевода должен обнулиться")
                .isZero();

        softly.assertThat(receiverAccAfter.getBalance())
                .as("Баланс получателя должен быть равен сумме перевода")
                .isEqualByComparingTo(transferAmount);

        softly.assertThat(receiverAccAfter.getBalance()).isEqualByComparingTo(depositedAccount.getBalance());

        assertTransferTransactions(senderTransactions, receiverTransactions);

        assertApiDaoTransferTransactions(senderAcc.getId(), senderTransactions);
        assertApiDaoTransferTransactions(receiverAcc.getId(), receiverTransactions);

    }

    @Test
    public void userCanTransferMoneyYourselfTest() {
        CreateUserRequest userRequest1 = AdminSteps.createUser();
        var user = new UserSteps(userRequest1);
        var sourceAcc = user.createAccount();
        var targetAcc = user.createAccount();

        var depositedAccount = user.depositRandomMoneyToAccount(sourceAcc);
        var transferAmount = depositedAccount.getBalance();

        user.transferMoney(sourceAcc, targetAcc, transferAmount);

        var sourceAccAfter = user.getAccount(sourceAcc.getId());
        var targetAccAfter = user.getAccount(targetAcc.getId());

        var sourceTransactions = user.getTransaction(sourceAcc.getId());
        var targetTransactions = user.getTransaction(targetAcc.getId());

        softly.assertThat(sourceAccAfter.getBalance())
                .as("Баланс счета-отправителя")
                .isZero();

        softly.assertThat(targetAccAfter.getBalance())
                .as("Баланс счета-получателя")
                .isEqualByComparingTo(transferAmount);

        assertTransferTransactions(sourceTransactions, targetTransactions);

        assertApiDaoTransferTransactions(sourceAcc.getId(), sourceTransactions);
        assertApiDaoTransferTransactions(targetAcc.getId(), targetTransactions);
    }

    @Test
    public void userCanTransferMinSumTest() {
        CreateUserRequest userRequest1 = AdminSteps.createUser();
        var user = new UserSteps(userRequest1);
        var sourceAcc = user.createAccount();
        var targetAcc = user.createAccount();

        var expectedTargetState = user.depositAccount(sourceAcc, 0.01);

        user.transferMoney(sourceAcc, targetAcc, expectedTargetState.getBalance());

        var sourceAccAfter = user.getAccount(sourceAcc.getId());
        var targetAccAfter = user.getAccount(targetAcc.getId());

        var sourceTransactions = user.getTransaction(sourceAcc.getId());
        var targetTransactions = user.getTransaction(targetAcc.getId());

        softly.assertThat(sourceAccAfter.getBalance())
                .as("Баланс отправителя после перевода минимальной суммы")
                .isZero();

        softly.assertThat(targetAccAfter.getBalance())
                .as("Баланс получателя после перевода минимальной суммы")
                .isEqualByComparingTo("0.01");

        assertTransferTransactions(sourceTransactions, targetTransactions);

        assertApiDaoTransferTransactions(sourceAcc.getId(), sourceTransactions);
        assertApiDaoTransferTransactions(targetAcc.getId(), targetTransactions);

    }

    @Test
    public void userCanTransferMaxSumTest() {
        CreateUserRequest userRequest = AdminSteps.createUser();
        var user = new UserSteps(userRequest);
        var sourceAcc = user.createAccount();
        var targetAcc = user.createAccount();

        user.depositAccount(sourceAcc, 5000);
        var expectedTargetState = user.depositAccount(sourceAcc, 5000);

        user.transferMoney(sourceAcc, targetAcc, expectedTargetState.getBalance());

        var sourceAccAfter = user.getAccount(sourceAcc.getId());
        var targetAccAfter = user.getAccount(targetAcc.getId());

        var sourceTransactions = user.getTransaction(sourceAcc.getId());
        var targetTransactions = user.getTransaction(targetAcc.getId());

        softly.assertThat(sourceAccAfter.getBalance())
                .as("Баланс отправителя после перевода максимальной суммы")
                .isZero();

        softly.assertThat(targetAccAfter.getBalance())
                .as("Баланс получателя после перевода максимальной суммы")
                .isEqualByComparingTo("10000");

        assertTransferTransactions(sourceTransactions, targetTransactions);

        assertApiDaoTransferTransactions(sourceAcc.getId(), sourceTransactions);
        assertApiDaoTransferTransactions(targetAcc.getId(), targetTransactions);

    }

    public static Stream<Arguments> invalidTransferData() {
        return Stream.of(
                Arguments.of(0.0, "Invalid transfer: insufficient funds or invalid accounts"),
                Arguments.of(-0.01, "Invalid transfer: insufficient funds or invalid accounts"),
                Arguments.of(10000.01, "Transfer amount cannot exceed 10000"),
                Arguments.of(200.00, "Invalid transfer: insufficient funds or invalid accounts")
        );
    }

    @MethodSource("invalidTransferData")
    @ParameterizedTest(name = "Негативные тесты")
    public void userCantTransferWithInvalidAmountTest(Double balance, String error) {
        CreateUserRequest userRequest = AdminSteps.createUser();
        var user = new UserSteps(userRequest);
        var sourceAcc = user.createAccount();
        var targetAcc = user.createAccount();


        TransferAccountRequest transferAccountRequest = TransferAccountRequest.builder()
                .senderAccountId(sourceAcc.getId())
                .receiverAccountId(targetAcc.getId())
                .amount(BigDecimal.valueOf(balance))
                .build();

        new CrudRequester(
                RequestSpecs.userSpec(userRequest.getUsername(), userRequest.getPassword()),
                Endpoint.ACCOUNTS_TRANSFER,
                ResponseSpecs.isBadRequest(null, error))
                .post(transferAccountRequest);

        var sourceAccAfter = user.getAccount(sourceAcc.getId());
        var targetAccAfter = user.getAccount(targetAcc.getId());

        softly.assertThat(sourceAccAfter.getBalance())
                .as("Баланс отправителя не должен измениться после ошибки")
                .isZero();

        softly.assertThat(targetAccAfter.getBalance())
                .as("Баланс получателя не должен измениться после ошибки")
                .isZero();

        softly.assertThat(user.getTransaction(sourceAcc.getId()))
                .as("У отправителя не должно появиться транзакций")
                .isEmpty();

        softly.assertThat(user.getTransaction(targetAcc.getId()))
                .as("У получателя не должно появиться транзакций")
                .isEmpty();

        softly.assertThat(DataBaseSteps.getTransactionByAccountId(sourceAcc.getId()))
                .as("У отправителя не должно появится транзакций в бд")
                .isEmpty();

        softly.assertThat(DataBaseSteps.getTransactionByAccountId(targetAcc.getId()))
                .as("У получателя не должно появится транзакций в бд")
                .isEmpty();

    }

    private void assertTransferTransactions(List<TransactionResponse> sourceTx, List<TransactionResponse> targetTx) {
        softly.assertThat(sourceTx)
                .extracting(TransactionResponse::getType)
                .as("История транзакций счета-отправителя")
                .contains(TransactionType.TRANSFER_OUT, TransactionType.DEPOSIT);

        softly.assertThat(targetTx)
                .extracting(TransactionResponse::getType)
                .as("История транзакций счета-получателя")
                .contains(TransactionType.TRANSFER_IN);
    }

    private void assertApiDaoTransferTransactions(Integer accountId, List<TransactionResponse> apitransactions) {
        var sourceTransactionsDao = DataBaseSteps.getTransactionByAccountId(accountId);
        DaoAndModelAssertions.assertThat(apitransactions, sourceTransactionsDao).match();
    }
}
