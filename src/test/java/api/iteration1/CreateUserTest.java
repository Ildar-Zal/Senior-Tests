package api.iteration1;

import base.BaseTest;
import api.generators.RandomModelGenerator;
import api.models.CreateUserRequest;
import api.models.UserResponse;
import api.models.comparison.ModelAssertions;
import common.annotations.WithValidationFix;
//import common.extansions.ValidationFixExtension;
import io.restassured.http.ContentType;
import org.apache.http.HttpStatus;
import org.apache.http.protocol.HTTP;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import api.requests.skelethon.Endpoint;
import api.requests.skelethon.requesters.CrudRequester;
import api.requests.skelethon.requesters.ValidatableCrudRequester;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;

import java.util.NoSuchElementException;
import java.util.stream.Stream;


public class CreateUserTest extends BaseTest {

    @WithValidationFix
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

