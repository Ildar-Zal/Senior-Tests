package iteration1;

import base.BaseTest;
import models.AccountResponse;
import models.CreateUserRequest;
import models.comparison.ModelAssertions;
import org.junit.jupiter.api.Test;
import requests.skelethon.Endpoint;
import requests.skelethon.requesters.CrudRequester;
import requests.skelethon.requesters.ValidatableCrudRequester;
import requests.steps.AdminSteps;
import specs.RequestSpecs;
import specs.ResponseSpecs;


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
    }
}
