package iteration2;

import api.generators.RandomModelGenerator;
import api.models.CreateUserRequest;
import api.models.UpdateCustomerProfileRequest;
import api.models.UpdateCustomerProfileResponse;
import api.models.UserResponse;
import api.models.comparison.ModelAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import api.requests.skelethon.Endpoint;
import api.requests.skelethon.requesters.CrudRequester;
import api.requests.skelethon.requesters.ValidatableCrudRequester;
import api.requests.steps.AdminSteps;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;

import java.util.stream.Stream;

public class UpdateUserNameTest {

    @Test
    public void UserCanChangeNameTest() {
        CreateUserRequest userRequest = AdminSteps.createUser();

        UpdateCustomerProfileRequest updateRequest = RandomModelGenerator.generate(UpdateCustomerProfileRequest.class);

        var expectedProfileState =
                new ValidatableCrudRequester<UpdateCustomerProfileResponse>
                        (RequestSpecs.userSpec(userRequest.getUsername(), userRequest.getPassword()),
                                Endpoint.UPDATE_CUSTOMER_PROFILE,
                                ResponseSpecs.isOk())
                        .put(null, updateRequest)
                        .getCustomer();

        var actualProfileAfterUpdate =
                new ValidatableCrudRequester<UserResponse>
                        (RequestSpecs.userSpec(userRequest.getUsername(), userRequest.getPassword()),
                                Endpoint.GET_CUSTOMER_PROFILE,
                                ResponseSpecs.isOk())
                        .get(null);

        ModelAssertions.assertThatModels(actualProfileAfterUpdate, expectedProfileState).match();
    }

    public static Stream<Arguments> invalidData() {
        return Stream.of(
                Arguments.of("asdc", "Name must contain two words with letters only"),
                Arguments.of("asdc123123 acvxc", "Name must contain two words with letters only"),
                Arguments.of("123123 adsasd", "Name must contain two words with letters only"),
                Arguments.of("ADASD #@$@#$@$#", "Name must contain two words with letters only"),
                Arguments.of("asdasd asdasd adasdasd", "Name must contain two words with letters only"),
                Arguments.of("adasdasd   ", "Name must contain two words with letters only"),
                Arguments.of("   adasdasd", "Name must contain two words with letters only"),
                Arguments.of("фвыфв фывфыв ", "Name must contain two words with letters only")
        );
    }

    @MethodSource("invalidData")
    @ParameterizedTest(name = "Негативные тесты")
    public void negativeTest(String name, String error) {
        CreateUserRequest userRequest = AdminSteps.createUser();

        UpdateCustomerProfileRequest updateCustomerProfileRequest = UpdateCustomerProfileRequest.builder()
                .name(name)
                .build();

        new CrudRequester
                (RequestSpecs.userSpec(userRequest.getUsername(), userRequest.getPassword()),
                        Endpoint.UPDATE_CUSTOMER_PROFILE,
                        ResponseSpecs.isBadRequest(null,error))
                .put(null, updateCustomerProfileRequest);
    }

}
