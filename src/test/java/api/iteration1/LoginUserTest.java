package api.iteration1;

import api.models.CreateUserRequest;
import api.models.LoginUserRequest;
import api.requests.skelethon.Endpoint;
import api.requests.skelethon.requesters.CrudRequester;
import api.requests.steps.AdminSteps;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;
import base.BaseTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;


public class LoginUserTest extends BaseTest {

    @Test
    public void userCanLoginTest() {
        CreateUserRequest userRequest = AdminSteps.createUser();

        new CrudRequester(RequestSpecs.unauthSpec(), Endpoint.AUTH_LOGIN, ResponseSpecs.isOk())
                .post(LoginUserRequest.builder().username(userRequest.getUsername())
                        .password(userRequest.getPassword()).build());
    }

    public static Stream<Arguments> invalidUserData() {
        return Stream.of(
                Arguments.of("   ", "Password33$", "message", "Invalid username or password"),
                Arguments.of("ab", "Password33$", "message", "Invalid username or password"),
                Arguments.of("abc$", "Password33$", "message", "Invalid username or password"),
                Arguments.of("abc%", "Password33$", "message", "Invalid username or password")
        );
    }

    @MethodSource("invalidUserData")
    @ParameterizedTest(name = "Негативные тесты")
    public void userCantLoginTest(String userName, String password, String errorKey, String errorValue) {
        LoginUserRequest loginUserRequest = LoginUserRequest.builder()
                .username(userName)
                .password(password)
                .build();

        new CrudRequester(RequestSpecs.unauthSpec(),Endpoint.AUTH_LOGIN,ResponseSpecs.isUnathorized(errorKey,errorValue))
                .post(loginUserRequest);
    }
}
