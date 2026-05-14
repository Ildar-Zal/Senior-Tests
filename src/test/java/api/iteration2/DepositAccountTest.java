package api.iteration2;

import api.generators.RandomModelGenerator;
import api.models.AccountResponse;
import api.models.CreateUserRequest;
import api.models.DepositAccountRequest;
import base.BaseTest;
import api.models.comparison.ModelAssertions;
import org.junit.jupiter.api.Test;
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

    @Test
    public void UserCanDepositAccountTest() {
        CreateUserRequest userRequest = AdminSteps.createUser();
        var user = new UserSteps(userRequest);
        var account = user.createAccount();

        DepositAccountRequest depositAccountRequest = RandomModelGenerator.generate(DepositAccountRequest.class);
        depositAccountRequest.setId(account.getId());

        var expectedAccountState = new ValidatableCrudRequester<AccountResponse>
                (RequestSpecs.userSpec(userRequest.getUsername(), userRequest.getPassword()),
                        Endpoint.ACCOUNTS_DEPOSIT,
                        ResponseSpecs.isOk())
                .post(depositAccountRequest);

        var accountAfterDeposit = user.getAccount(expectedAccountState.getAccountNumber());

        ModelAssertions.assertThatModels(expectedAccountState, accountAfterDeposit).match();
    }

    @Test
    public void UserCanDepositMaxSumAccountTest() {
        var userRequest = AdminSteps.createUser();
        var user = new UserSteps(userRequest);
        var account = user.createAccount();

        var expectedAccountState = user.depositAccount(account, 5000);
        var accountAfterDeposit = user.getAccount(expectedAccountState.getAccountNumber());

        ModelAssertions.assertThatModels(expectedAccountState, accountAfterDeposit).match();
    }

    @Test
    public void UserCanDepositMinSumAccountTest() {
        var userRequest = AdminSteps.createUser();
        var user = new UserSteps(userRequest);
        var account = user.createAccount();

        var expectedAccountState = user.depositAccount(account, 0.01);
        var accountAfterDeposit = user.getAccount(expectedAccountState.getAccountNumber());

        ModelAssertions.assertThatModels(expectedAccountState, accountAfterDeposit).match();
    }

    @Test
    public void userCantDepositToNonExistAccount() {
        CreateUserRequest userRequest = AdminSteps.createUser();
        DepositAccountRequest depositAccountRequest = RandomModelGenerator.generate(DepositAccountRequest.class);
        depositAccountRequest.setId(123123132);

        new CrudRequester(RequestSpecs.userSpec(userRequest.getUsername(), userRequest.getPassword()),
                Endpoint.ACCOUNTS_DEPOSIT,
                ResponseSpecs.isForbidden(null, "Unauthorized access to account"))
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
                ResponseSpecs.isForbidden(null, "Unauthorized access to account"))
                .post(depositAccountRequest);

    }

    public static Stream<Arguments> invalidDepositData() {
        return Stream.of(
                Arguments.of(5000.01, "Deposit amount cannot exceed 5000"),
                Arguments.of(0.0, "Deposit amount must be at least 0.01"),
                Arguments.of(-0.01, "Deposit amount must be at least 0.01")
        );
    }

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
    }
}
