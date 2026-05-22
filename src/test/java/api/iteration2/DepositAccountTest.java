package api.iteration2;

import api.generators.RandomModelGenerator;
import api.models.AccountResponse;
import api.models.CreateUserRequest;
import api.models.DepositAccountRequest;
import base.BaseTest;
import api.models.comparison.ModelAssertions;
import common.annotations.WithValidationFix;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import api.requests.skelethon.Endpoint;
import api.requests.skelethon.requesters.CrudRequester;
import api.requests.skelethon.requesters.ValidatableCrudRequester;
import api.requests.steps.AdminSteps;
import api.requests.steps.UserSteps;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;

import java.math.BigDecimal;
import java.util.stream.Stream;

public class DepositAccountTest extends BaseTest {

    @WithValidationFix
    @Test
    public void userCanDepositAccountTest() {
        CreateUserRequest userRequest = AdminSteps.createUser();
        var user = new UserSteps(userRequest);
        var account = user.createAccount();
        var balanceBeforeDeposit = account.getBalance();

        DepositAccountRequest depositAccountRequest = RandomModelGenerator.generate(DepositAccountRequest.class);
        depositAccountRequest.setId(account.getId());

        var expectedAccountState = new ValidatableCrudRequester<AccountResponse>
                (RequestSpecs.userSpec(userRequest.getUsername(), userRequest.getPassword()),
                        Endpoint.ACCOUNTS_DEPOSIT,
                        ResponseSpecs.isOk())
                .post(depositAccountRequest);

        var accountAfterDeposit = user.getAccount(expectedAccountState.getId());

        softly.assertThat(balanceBeforeDeposit).isEqualTo(BigDecimal.valueOf(0.0));
        ModelAssertions.assertThatModels(expectedAccountState, accountAfterDeposit).match();
    }

    @WithValidationFix
    @Test
    public void userCanDepositMaxSumAccountTest() {
        var userRequest = AdminSteps.createUser();
        var user = new UserSteps(userRequest);
        var account = user.createAccount();

        var expectedAccountState = user.depositAccount(account, 5000);
        var accountAfterDeposit = user.getAccount(expectedAccountState.getId());

        ModelAssertions.assertThatModels(expectedAccountState, accountAfterDeposit).match();
    }

    @WithValidationFix
    @Test
    public void userCanDepositMinSumAccountTest() {
        var userRequest = AdminSteps.createUser();
        var user = new UserSteps(userRequest);
        var account = user.createAccount();

        var expectedAccountState = user.depositAccount(account, 0.01);
        var accountAfterDeposit = user.getAccount(expectedAccountState.getId());

        ModelAssertions.assertThatModels(expectedAccountState, accountAfterDeposit).match();
    }

    @Test
    public void userCantDepositToNonExistAccount() {
        CreateUserRequest userRequest = AdminSteps.createUser();
        DepositAccountRequest depositAccountRequest = RandomModelGenerator.generate(DepositAccountRequest.class);
        depositAccountRequest.setId(123123132);

        new CrudRequester(RequestSpecs.userSpec(userRequest.getUsername(), userRequest.getPassword()),
                Endpoint.ACCOUNTS_DEPOSIT,
                ResponseSpecs.isForbidden())
                .post(depositAccountRequest);

    }

    @Test
    public void userCantDepositToDiffAccount() {
        CreateUserRequest userRequest = AdminSteps.createUser();
        CreateUserRequest userRequest1 = AdminSteps.createUser();
        var user = new UserSteps(userRequest1);
        var diffAccount = user.createAccount();

        DepositAccountRequest depositAccountRequest = RandomModelGenerator.generate(DepositAccountRequest.class);
        depositAccountRequest.setId(diffAccount.getId());

        new CrudRequester(RequestSpecs.userSpec(userRequest.getUsername(), userRequest.getPassword()),
                Endpoint.ACCOUNTS_DEPOSIT,
                ResponseSpecs.isForbidden())
                .post(depositAccountRequest);

        var diffAccountAfterDeposit = user.getAccount(diffAccount.getId());

        softly.assertThat(diffAccountAfterDeposit.getBalance())
                .as("Чужой аккаунт не должен быть пополнен")
                .isZero();
    }

    public static Stream<Arguments> invalidDepositData() {
        return Stream.of(
                Arguments.of(5000.01, "Deposit amount cannot exceed 5000"),
                Arguments.of(0.0, "Deposit amount must be at least 0.01"),
                Arguments.of(-0.01, "Deposit amount must be at least 0.01")
        );
    }

    @WithValidationFix
    @MethodSource("invalidDepositData")
    @ParameterizedTest(name = "Негативные тесты")
    public void userCantDepositAccountTest(Double balance, String error) {
        CreateUserRequest userRequest = AdminSteps.createUser();
        UserSteps user = new UserSteps(userRequest);
        var account = user.createAccount();

        DepositAccountRequest depositAccountRequest = DepositAccountRequest.builder()
                .balance(BigDecimal.valueOf(balance))
                .id(account.getId())
                .build();

        new CrudRequester(RequestSpecs.userSpec(userRequest.getUsername(), userRequest.getPassword()),
                Endpoint.ACCOUNTS_DEPOSIT,
                ResponseSpecs.isBadRequest(null, error))
                .post(depositAccountRequest);

        var accountAfterDeposit = user.getAccount(account.getId());
        softly.assertThat(accountAfterDeposit.getBalance())
                .as("Депозит не должен быть больше 5000 и меньше 0.01")
                .isEqualTo(BigDecimal.valueOf(0.0));
    }
}
