package api.requests.steps;

import api.generators.RandomModelGenerator;
import api.models.CreateUserRequest;
import api.models.UserResponse;
import api.requests.skelethon.Endpoint;
import api.requests.skelethon.requesters.CrudRequester;
import api.requests.skelethon.requesters.ValidatableCrudRequester;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;

import java.util.List;
import java.util.NoSuchElementException;

public class AdminSteps {

    public static CreateUserRequest createUser() {
        CreateUserRequest createUserRequest = RandomModelGenerator.generate(CreateUserRequest.class);

        new CrudRequester(RequestSpecs.adminSpec(), Endpoint.ADMIN_USERS, ResponseSpecs.isCreated())
                .post(createUserRequest);

        return createUserRequest;

    }

    public static List<UserResponse> getAllUsers() {
        return new ValidatableCrudRequester<UserResponse>(
                RequestSpecs.adminSpec(),
                Endpoint.ADMIN_USERS,
                ResponseSpecs.isOk()).getAll(UserResponse.class);
    }

    public static UserResponse getUser(CreateUserRequest user) {
        var allUsers = getAllUsers();
        return allUsers.stream()
                .filter(u -> user.getUsername().equals(u.getUsername()))
                .findFirst().
                orElseThrow(() -> new NoSuchElementException("User not found: " + user.getUsername()));
    }
}
