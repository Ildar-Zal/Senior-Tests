package api.iteration2;

import api.dao.AccountDao;
import api.dao.comparison.DaoAndModelAssertions;
import api.generators.RandomModelGenerator;
import api.models.AccountResponse;
import api.models.CreateUserRequest;
import api.models.DepositAccountRequest;
import api.models.TransferAccountRequest;
import api.models.comparison.ModelAssertions;
import api.requests.skelethon.Endpoint;
import api.requests.skelethon.requesters.CrudRequester;
import api.requests.skelethon.requesters.ValidatableCrudRequester;
import api.requests.steps.AdminSteps;
import api.requests.steps.DataBaseSteps;
import api.requests.steps.UserSteps;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;
import base.BaseTest;
import io.qameta.allure.Description;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.stream.Stream;

public class DepositAccountTest extends BaseTest {

    @Test
    public void userCanDepositAccountTest() {
        CreateUserRequest userRequest = AdminSteps.createUser();
        var user = new UserSteps(userRequest);
        var account = user.createAccount();
        var balanceBeforeDeposit = account.getBalance();

        DepositAccountRequest depositAccountRequest = RandomModelGenerator.generate(DepositAccountRequest.class);
        depositAccountRequest.setAccountId(account.getId());

        var expectedAccountState = new ValidatableCrudRequester<AccountResponse>
                (RequestSpecs.userSpec(userRequest.getUsername(), userRequest.getPassword()),
                        Endpoint.ACCOUNTS_DEPOSIT,
                        ResponseSpecs.isOk())
                .post(depositAccountRequest);

        var accountAfterDeposit = user.getAccount(expectedAccountState.getId());

        softly.assertThat(balanceBeforeDeposit).isEqualByComparingTo(BigDecimal.valueOf(0.0));
        ModelAssertions.assertThatModels(expectedAccountState, accountAfterDeposit).match();

        assertApiDaoAccountDeposit(accountAfterDeposit.getAccountNumber(), accountAfterDeposit);

    }

    @Test
    public void userCanDepositMaxSumAccountTest() {
        var userRequest = AdminSteps.createUser();
        var user = new UserSteps(userRequest);
        var account = user.createAccount();

        var expectedAccountState = user.depositAccount(account, 5000);
        var accountAfterDeposit = user.getAccount(expectedAccountState.getId());

        ModelAssertions.assertThatModels(expectedAccountState, accountAfterDeposit).match();

        assertApiDaoAccountDeposit(accountAfterDeposit.getAccountNumber(), accountAfterDeposit);

    }

    @Test
    public void userCanDepositMinSumAccountTest() {
        var userRequest = AdminSteps.createUser();
        var user = new UserSteps(userRequest);
        var account = user.createAccount();

        var expectedAccountState = user.depositAccount(account, 0.01);
        var accountAfterDeposit = user.getAccount(expectedAccountState.getId());

        ModelAssertions.assertThatModels(expectedAccountState, accountAfterDeposit).match();

        assertApiDaoAccountDeposit(accountAfterDeposit.getAccountNumber(), accountAfterDeposit);
    }

    @Test
    public void userCantDepositToNonExistAccount() {
        CreateUserRequest userRequest = AdminSteps.createUser();
        DepositAccountRequest depositAccountRequest = RandomModelGenerator.generate(DepositAccountRequest.class);
        depositAccountRequest.setAccountId(123123132);

        new CrudRequester(RequestSpecs.userSpec(userRequest.getUsername(), userRequest.getPassword()),
                Endpoint.ACCOUNTS_DEPOSIT,
                ResponseSpecs.isForbidden())
                .post(depositAccountRequest);

        softly.assertThat(DataBaseSteps.getAccountByAccountNumber(String.valueOf(123123132)))
                .as("Аккаунт должен отсутствовать в БД")
                .isNull();

    }

    @Test
    public void userCantDepositToDiffAccount() {
        CreateUserRequest userRequest = AdminSteps.createUser();
        CreateUserRequest userRequest1 = AdminSteps.createUser();
        var user = new UserSteps(userRequest1);
        var diffAccount = user.createAccount();

        DepositAccountRequest depositAccountRequest = RandomModelGenerator.generate(DepositAccountRequest.class);
        depositAccountRequest.setAccountId(diffAccount.getId());

        new CrudRequester(RequestSpecs.userSpec(userRequest.getUsername(), userRequest.getPassword()),
                Endpoint.ACCOUNTS_DEPOSIT,
                ResponseSpecs.isForbidden())
                .post(depositAccountRequest);

        var diffAccountAfterDeposit = user.getAccount(diffAccount.getId());

        softly.assertThat(diffAccountAfterDeposit.getBalance())
                .as("Чужой аккаунт не должен быть пополнен")
                .isZero();

        assertApiDaoAccountDeposit(diffAccountAfterDeposit.getAccountNumber(), diffAccountAfterDeposit);

    }

    public static Stream<Arguments> invalidDepositData() {
        return Stream.of(
                Arguments.of(5000.01, "message", "Deposit amount exceeds the 5000 limit"),
                Arguments.of(0.0, "message", "Invalid account or amount"),
                Arguments.of(-0.01, "message", "Invalid account or amount")
        );
    }

    @MethodSource("invalidDepositData")
    @ParameterizedTest(name = "Негативные тесты")
    public void userCantDepositAccountTest(Double balance, String errorKey, String error) {
        CreateUserRequest userRequest = AdminSteps.createUser();
        UserSteps user = new UserSteps(userRequest);
        var account = user.createAccount();

        DepositAccountRequest depositAccountRequest = DepositAccountRequest.builder()
                .amount(BigDecimal.valueOf(balance))
                .accountId(account.getId())
                .build();

        new CrudRequester(RequestSpecs.userSpec(userRequest.getUsername(), userRequest.getPassword()),
                Endpoint.ACCOUNTS_DEPOSIT,
                ResponseSpecs.isBadRequest(errorKey, error))
                .post(depositAccountRequest);

        var accountAfterDeposit = user.getAccount(account.getId());

        softly.assertThat(accountAfterDeposit.getBalance())
                .as("Депозит не должен быть больше 5000 и меньше 0.01")
                .isEqualTo(BigDecimal.valueOf(0.0));

        assertApiDaoAccountDeposit(accountAfterDeposit.getAccountNumber(), accountAfterDeposit);
    }

    private void assertApiDaoAccountDeposit(String accountId, AccountResponse accountApi) {
        AccountDao accountDao = DataBaseSteps.getAccountByAccountNumber(accountId);
        DaoAndModelAssertions.assertThat(accountApi, accountDao).match();
    }

    @Test
    @Description("Покрытие Swagger Coverage: 401 Unauthorized для /deposit")
    public void unauthorizedDepositError() {
        DepositAccountRequest depositAccountRequest = DepositAccountRequest.builder()
                .amount(BigDecimal.valueOf(1))
                .accountId(1)
                .build();

        new CrudRequester(RequestSpecs.unauthSpec(),
                Endpoint.ACCOUNTS_DEPOSIT,
                ResponseSpecs.isUnathorized())
                .post(depositAccountRequest);
    }
}
