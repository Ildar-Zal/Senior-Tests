package iteration1.api;

import base.BaseTest;
import generators.RandomModelGenerator;
import models.CreateUserRequest;
import models.UserResponse;
import models.comparison.ModelAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import requests.skelethon.Endpoint;
import requests.skelethon.requesters.CrudRequester;
import requests.skelethon.requesters.ValidatableCrudRequester;
import specs.RequestSpecs;
import specs.ResponseSpecs;

import java.util.NoSuchElementException;
import java.util.stream.Stream;


public class CreateUserTest extends BaseTest {

    @Test
    public void adminCanCreateUserTest() {
        CreateUserRequest createUserRequest = RandomModelGenerator.generate(CreateUserRequest.class);

        var createdUser = new ValidatableCrudRequester<UserResponse>(RequestSpecs.adminSpec(),
                Endpoint.ADMIN_USERS, ResponseSpecs.isCreated())
                .post(createUserRequest);

        var allUsers = new CrudRequester(RequestSpecs.adminSpec(), Endpoint.ADMIN_USERS, ResponseSpecs.isOk())
                .get(null).extract().jsonPath().getList("", UserResponse.class);

        var foundUser = allUsers.stream()
                .filter(u -> createdUser.getUsername().equals(u.getUsername()))
                .findFirst().
                orElseThrow(() -> new NoSuchElementException("User not found: " + createdUser.getUsername()));

        ModelAssertions.assertThatModels(createdUser, foundUser).match();
    }

    public static Stream<Arguments> userInvalidData() {
        return Stream.of(
                Arguments.of("   ", "Password33$", "USER", "username", "Username cannot be blank"),
                Arguments.of("ab", "Password33$", "USER", "username", "Username must be between 3 and 15 characters"),
                Arguments.of("abc$", "Password33$", "USER", "username", "Username must contain only letters, digits, dashes, underscores, and dots"),
                Arguments.of("abc%", "Password33$", "USER", "username", "Username must contain only letters, digits, dashes, underscores, and dots"),
                Arguments.of("фыфывФЫВЫФВ1", "Password33$", "USER", "username", "Username must contain only letters, digits, dashes, underscores, and dots")
        );
    }

    @MethodSource("userInvalidData")
    @ParameterizedTest(name = "Негативные тесты")
    public void adminCantCreateUserTest(String username, String password, String role, String errorKey, String errorValue) {
        new CrudRequester(RequestSpecs.adminSpec(),
                Endpoint.ADMIN_USERS, ResponseSpecs.isBadRequest(errorKey, errorValue))
                .post(CreateUserRequest.builder().username(username).password(password).role(role).build());
    }
}

