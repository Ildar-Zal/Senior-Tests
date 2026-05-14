package requests.steps;

import generators.RandomModelGenerator;
import models.CreateUserRequest;
import requests.skelethon.Endpoint;
import requests.skelethon.requesters.CrudRequester;
import specs.RequestSpecs;
import specs.ResponseSpecs;

public class AdminSteps {

    public static CreateUserRequest createUser() {
        CreateUserRequest createUserRequest = RandomModelGenerator.generate(CreateUserRequest.class);

        new CrudRequester(RequestSpecs.adminSpec(), Endpoint.ADMIN_USERS, ResponseSpecs.isCreated())
                .post(createUserRequest);

        return createUserRequest;
    }
}
