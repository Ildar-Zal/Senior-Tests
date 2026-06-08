package api.iteration1;

import api.dao.AccountDao;
import api.dao.comparison.DaoAndModelAssertions;
import api.models.AccountResponse;
import api.models.CreateUserRequest;
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

import java.math.BigDecimal;

public class CreateAccountTest extends BaseTest {

    @Test
    public void userCanCreateAccountTest() {
        CreateUserRequest userRequest = AdminSteps.createUser();

        var createdAccount = new ValidatableCrudRequester<AccountResponse>
                (RequestSpecs.userSpec(userRequest.getUsername(), userRequest.getPassword()),
                        Endpoint.ACCOUNTS,
                        ResponseSpecs.isCreated())
                .post(null);

        var accounts = new CrudRequester
                (RequestSpecs.userSpec(userRequest.getUsername(), userRequest.getPassword()),
                        Endpoint.CUSTOMER_ACCOUNTS,
                        ResponseSpecs.isOk())
                .get(null)
                .extract()
                .jsonPath()
                .getList("", AccountResponse.class);

        var foundAccount = accounts.stream().filter(a -> a.getId().equals(createdAccount.getId())).findFirst().orElseThrow();
        ModelAssertions.assertThatModels(createdAccount, foundAccount).match();

        AccountDao accountDao = DataBaseSteps.getAccountByAccountNumber(foundAccount.getAccountNumber());

        DaoAndModelAssertions.assertThat(foundAccount, accountDao).match();
    }

//    @Test
//    @Description("Покрытие Swagger Coverage: 401 Unauthorized для GET /accounts")
//    public void unauthorizedAccountsError() {
//        new CrudRequester
//                (RequestSpecs.unauthSpec(),
//                        Endpoint.ACCOUNTS,
//                        ResponseSpecs.isUnathorized())
//                .getAll(AccountResponse.class);
//    }
//
//    @Test
//    @Description("Покрытие Swagger Coverage: 401 Unauthorized для customer/accounts")
//    public void unauthorizedCustomerAccountsError() {
//        new CrudRequester
//                (RequestSpecs.unauthSpec(),
//                        Endpoint.CUSTOMER_ACCOUNTS,
//                        ResponseSpecs.isUnathorized())
//                .getAll(AccountResponse.class);
//    }
//    @Test
//    @Description("Покрытие Swagger Coverage: 401 Unauthorized для POST /accounts")
//    public void unauthorizedPostAccountsError() {
//
//        new CrudRequester
//                (RequestSpecs.unauthSpec(),
//                        Endpoint.ACCOUNTS,
//                        ResponseSpecs.isUnathorized())
//                .post(null);
//    }


}
