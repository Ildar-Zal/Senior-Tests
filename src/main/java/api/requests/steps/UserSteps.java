package api.requests.steps;

import api.generators.RandomModelGenerator;
import api.models.*;
import io.restassured.specification.RequestSpecification;
import api.requests.skelethon.Endpoint;
import api.requests.skelethon.requesters.CrudRequester;
import api.requests.skelethon.requesters.ValidatableCrudRequester;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;

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
        return new ValidatableCrudRequester<AccountResponse>
                (userSpec, Endpoint.ACCOUNTS, ResponseSpecs.isCreated())
                .post(null);
    }

    public AccountResponse depositAccount(AccountResponse account, double balance) {
        DepositAccountRequest depositAccountRequest = DepositAccountRequest
                .builder()
                .balance(BigDecimal.valueOf(balance))
                .id(account.getId())
                .build();

        return new ValidatableCrudRequester<AccountResponse>
                (userSpec, Endpoint.ACCOUNTS_DEPOSIT, ResponseSpecs.isOk())
                .post(depositAccountRequest);

    }


    public AccountResponse getAccount(Integer id) {
        List<AccountResponse> userAccounts = getAccounts();
        return userAccounts.stream()
                .filter(a -> a.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        String.format("Account with Account id %d not found for user: %s", id, user.getUsername())
                ));
    }

    public AccountResponse depositRandomMoneyToAccount(AccountResponse account) {
        DepositAccountRequest randomRequest = RandomModelGenerator.generate(DepositAccountRequest.class);
        return depositAccount(account, randomRequest.getBalance().doubleValue());
    }

    public void transferMoney(AccountResponse sourceAcc, AccountResponse targetAcc, BigDecimal money) {
        TransferAccountRequest transferAccountRequest = TransferAccountRequest.builder()
                .senderAccountId(sourceAcc.getId())
                .receiverAccountId(targetAcc.getId())
                .amount(money)
                .build();

        new CrudRequester(
                userSpec, Endpoint.ACCOUNTS_TRANSFER, ResponseSpecs.isOk())
                .post(transferAccountRequest);
    }


    public List<AccountResponse> getAccounts() {
        return new CrudRequester
                (userSpec, Endpoint.CUSTOMER_ACCOUNTS, ResponseSpecs.isOk())
                .get(null)
                .extract()
                .jsonPath()
                .getList("", AccountResponse.class);
    }

    public UserResponse getUserProfile() {
        return new ValidatableCrudRequester<UserResponse>
                (userSpec,
                        Endpoint.GET_CUSTOMER_PROFILE,
                        ResponseSpecs.isOk())
                .get(null);
    }

    public List<TransactionResponse> getTransaction(Integer id) {
        return new ValidatableCrudRequester<TransactionResponse>
                (userSpec,
                        Endpoint.ACCOUNTS_TRANSACTIONS,
                        ResponseSpecs.isOk()).getList(id);
    }
}
