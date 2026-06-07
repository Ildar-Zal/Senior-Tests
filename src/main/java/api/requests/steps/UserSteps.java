package api.requests.steps;

import api.generators.RandomModelGenerator;
import api.models.*;
import api.requests.skelethon.Endpoint;
import api.requests.skelethon.requesters.CrudRequester;
import api.requests.skelethon.requesters.ValidatableCrudRequester;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;
import common.helpers.StepLogger;
import io.restassured.specification.RequestSpecification;

import java.math.BigDecimal;
import java.util.List;

public class UserSteps {

    private final CreateUserRequest user;
    private final RequestSpecification userSpec;

    public UserSteps(CreateUserRequest user) {
        this.user = user;
        this.userSpec = RequestSpecs.userSpec(user.getUsername(), user.getPassword());
    }

    public AccountResponse createAccount() {
        return StepLogger.log("User " + user.getUsername() + " create account", () -> {
            return new ValidatableCrudRequester<AccountResponse>(userSpec,
                    Endpoint.ACCOUNTS,
                    ResponseSpecs.isCreated())
                    .post(null);
        });
    }

    public AccountResponse depositAccount(AccountResponse account, double balance) {
        DepositAccountRequest depositAccountRequest = DepositAccountRequest
                .builder()
                .amount(BigDecimal.valueOf(balance))
                .accountId(account.getId())
                .build();

        return StepLogger.log("User " + user.getUsername() + " deposit account", () -> {
            return new ValidatableCrudRequester<AccountResponse>(userSpec,
                    Endpoint.ACCOUNTS_DEPOSIT,
                    ResponseSpecs.isOk())
                    .post(depositAccountRequest);
        });

    }


    public AccountResponse getAccount(Integer id) {
        List<AccountResponse> userAccounts = getAccounts();
        return StepLogger.log("User " + user.getUsername() + " get  account by id: " + id, () -> {
            return userAccounts.stream()
                    .filter(a -> a.getId().equals(id))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException(
                            String.format("Account with Account id %d not found for user: %s", id, user.getUsername())
                    ));
        });
    }

    public AccountResponse depositRandomMoneyToAccount(AccountResponse account) {
        DepositAccountRequest randomRequest = RandomModelGenerator.generate(DepositAccountRequest.class);
        return StepLogger.log("User " + user.getUsername() + " deposit money to account: " + account.getAccountNumber(), () -> {
            return depositAccount(account, randomRequest.getAmount().doubleValue());
        });
    }

    public void transferMoney(AccountResponse sourceAcc, AccountResponse targetAcc, BigDecimal money) {
        TransferAccountRequest transferAccountRequest = TransferAccountRequest.builder()
                .senderAccountId(sourceAcc.getId())
                .receiverAccountId(targetAcc.getId())
                .amount(money)
                .build();

        StepLogger.log("User " + user.getUsername() + " transfers " + money + " to " + targetAcc + " with fraud check", () -> {
            new CrudRequester(userSpec,
                    Endpoint.ACCOUNTS_TRANSFER,
                    ResponseSpecs.isOk())
                    .post(transferAccountRequest);
        });
    }


    public List<AccountResponse> getAccounts() {
        return StepLogger.log("User " + user.getUsername() + " get all accounts", () -> {
            return new ValidatableCrudRequester<AccountResponse>(userSpec,
                    Endpoint.CUSTOMER_ACCOUNTS,
                    ResponseSpecs.isOk())
                    .getAll(AccountResponse.class);
        });
    }

    public UserResponse getUserProfile() {
        return StepLogger.log("User " + user.getUsername() + " get user profile", () -> {
            return new ValidatableCrudRequester<UserResponse>(userSpec,
                    Endpoint.GET_CUSTOMER_PROFILE,
                    ResponseSpecs.isOk())
                    .get(null);
        });
    }

    public List<TransactionResponse> getTransaction(Integer id) {
        return StepLogger.log("User " + user.getUsername() + " get transaction", () -> {
            return new ValidatableCrudRequester<TransactionResponse>(userSpec,
                    Endpoint.ACCOUNTS_TRANSACTIONS,
                    ResponseSpecs.isOk()).getList(id);
        });
    }

    public TransferResponse transferWithFraudCheck(Integer senderAccountId, Integer receiverAccountId, BigDecimal amount) {
        return StepLogger.log("User " + user.getUsername() + " transfers " + amount + " to " + receiverAccountId + " with fraud check", () -> {
            TransferRequest transferRequest = TransferRequest.builder()
                    .senderAccountId(senderAccountId)
                    .receiverAccountId(receiverAccountId)
                    .amount(amount)
                    .description("Test transfer with fraud check")
                    .build();

            return new ValidatableCrudRequester<TransferResponse>(
                    RequestSpecs.userSpec(user.getUsername(), user.getPassword()),
                    Endpoint.TRANSFER_WITH_FRAUD_CHECK,
                    ResponseSpecs.isOk()).post(transferRequest);
        });
    }
}
