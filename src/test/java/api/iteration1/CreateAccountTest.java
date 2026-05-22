package api.iteration1;

import base.BaseTest;
import api.models.AccountResponse;
import api.models.CreateUserRequest;
import api.models.comparison.ModelAssertions;
import common.annotations.WithValidationFix;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import api.requests.skelethon.Endpoint;
import api.requests.skelethon.requesters.CrudRequester;
import api.requests.skelethon.requesters.ValidatableCrudRequester;
import api.requests.steps.AdminSteps;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

public class CreateAccountTest extends BaseTest {

    @WithValidationFix
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
