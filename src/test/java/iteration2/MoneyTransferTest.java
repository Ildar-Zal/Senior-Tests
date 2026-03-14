package iteration2;

import base.BaseTest;
import models.*;
import models.comparison.ModelAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import requests.skelethon.Endpoint;
import requests.skelethon.requesters.CrudRequester;
import requests.steps.AdminSteps;
import requests.steps.UserSteps;
import specs.RequestSpecs;
import specs.ResponseSpecs;

import java.math.BigDecimal;
import java.util.stream.Stream;


public class MoneyTransferTest extends BaseTest {

    @Test
    public void UserCanTransferMoneyDifferentUserTest() {
        CreateUserRequest userRequest1 = AdminSteps.createUser();
        CreateUserRequest userRequest2 = AdminSteps.createUser();
        var sender = new UserSteps(userRequest1);
        var receiver = new UserSteps(userRequest2);
        var senderAcc = sender.createAccount();
        var receiverAcc = receiver.createAccount();

        var expectedReceiverState = sender.depositRandomMoneyToAccount(senderAcc);

        TransferAccountRequest transferRequest = TransferAccountRequest.builder()
                .senderAccountId(senderAcc.getId())
                .receiverAccountId(receiverAcc.getId())
                .amount(expectedReceiverState.getBalance())
                .build();

        new CrudRequester(
                RequestSpecs.userSpec(userRequest1.getUsername(), userRequest1.getPassword()),
                Endpoint.ACCOUNTS_TRANSFER,
                ResponseSpecs.isOk())
                .post(transferRequest);

        var senderAccAfterTransfer = sender.getAccount(senderAcc.getId());
        var receiverAccAfterTransfer = receiver.getAccount(receiverAcc.getId());

        softly.assertThat(senderAccAfterTransfer.getBalance())
                .as("Баланс отправителя после перевода")
                .isZero();

        ModelAssertions.assertThatModels(receiverAccAfterTransfer, expectedReceiverState)
                .ignoringFields("id", "accountNumber", "transactions")
                .match();
    }

    @Test
    public void UserCanTransferMoneyYourselfTest() {
        CreateUserRequest userRequest1 = AdminSteps.createUser();
        var user = new UserSteps(userRequest1);
        var sourceAcc = user.createAccount();
        var targetAcc = user.createAccount();

        var expectedTargetState = user.depositRandomMoneyToAccount(sourceAcc);

        user.transferMoney(sourceAcc, targetAcc, expectedTargetState.getBalance());

        var sourceAccAfterTransfer = user.getAccount(sourceAcc.getId());
        var targetAccAfterTransfer = user.getAccount(targetAcc.getId());

        softly.assertThat(sourceAccAfterTransfer.getBalance())
                .as("Баланс отправителя после перевода")
                .isZero();

        ModelAssertions.assertThatModels(targetAccAfterTransfer, expectedTargetState)
                .ignoringFields("id", "accountNumber","transactions")
                .match();
    }

    @Test
    public void UserCanTransferMinSumTest() {
        CreateUserRequest userRequest1 = AdminSteps.createUser();
        var user = new UserSteps(userRequest1);
        var sourceAcc = user.createAccount();
        var targetAcc = user.createAccount();

        var expectedTargetState = user.depositAccount(sourceAcc, 0.01);

        user.transferMoney(sourceAcc, targetAcc, expectedTargetState.getBalance());

        var sourceAccAfterTransfer = user.getAccount(sourceAcc.getId());
        var targetAccAfterTransfer = user.getAccount(targetAcc.getId());

        softly.assertThat(sourceAccAfterTransfer.getBalance())
                .as("Баланс отправителя после перевода")
                .isZero();
        ModelAssertions.assertThatModels(targetAccAfterTransfer, expectedTargetState)
                .ignoringFields("id", "accountNumber", "transactions")
                .match();
    }

    @Test
    public void UserCanTransferMaxSumTest() {
        CreateUserRequest userRequest = AdminSteps.createUser();
        var user = new UserSteps(userRequest);
        var sourceAcc = user.createAccount();
        var targetAcc = user.createAccount();

        user.depositAccount(sourceAcc, 5000);
        var expectedTargetState = user.depositAccount(sourceAcc, 5000);

        user.transferMoney(sourceAcc, targetAcc, expectedTargetState.getBalance());

        var sourceAccAfterTransfer = user.getAccount(sourceAcc.getId());
        var targetAccAfterTransfer = user.getAccount(targetAcc.getId());

        softly.assertThat(sourceAccAfterTransfer.getBalance())
                .as("Баланс отправителя после перевода")
                .isZero();

        ModelAssertions.assertThatModels(targetAccAfterTransfer, expectedTargetState)
                .ignoringFields("id", "accountNumber","transactions")
                .match();

    }

    public static Stream<Arguments> invalidTransferData() {
        return Stream.of(
                Arguments.of(0.0, "Transfer amount must be at least 0.01"),
                Arguments.of(-0.01, "Transfer amount must be at least 0.01"),
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
    }
}
