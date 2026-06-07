package api.iteration2;

import api.dao.UserDao;
import api.dao.comparison.DaoAndModelAssertions;
import api.generators.RandomModelGenerator;
import api.models.CreateUserRequest;
import api.models.UpdateCustomerProfileRequest;
import api.models.UpdateCustomerProfileResponse;
import api.models.UserResponse;
import api.requests.skelethon.Endpoint;
import api.requests.skelethon.requesters.CrudRequester;
import api.requests.skelethon.requesters.ValidatableCrudRequester;
import api.requests.steps.AdminSteps;
import api.requests.steps.DataBaseSteps;
import api.requests.steps.UserSteps;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;
import base.BaseTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

public class UpdateUserNameTest extends BaseTest {

    @Test
    public void userCanChangeNameTest() {
        CreateUserRequest userRequest = AdminSteps.createUser();
        var user = new UserSteps(userRequest);
        var nameBeforeUpdate = user.getUserProfile().getName();

        UpdateCustomerProfileRequest updateRequest = RandomModelGenerator.generate(UpdateCustomerProfileRequest.class);

        var expectedName =
                new ValidatableCrudRequester<UpdateCustomerProfileResponse>
                        (RequestSpecs.userSpec(userRequest.getUsername(), userRequest.getPassword()),
                                Endpoint.UPDATE_CUSTOMER_PROFILE,
                                ResponseSpecs.isOk())
                        .put(updateRequest)
                        .getCustomer().getName();

        var actualName =
                new ValidatableCrudRequester<UserResponse>
                        (RequestSpecs.userSpec(userRequest.getUsername(), userRequest.getPassword()),
                                Endpoint.GET_CUSTOMER_PROFILE,
                                ResponseSpecs.isOk())
                        .get(null).getName();

        softly.assertThat(nameBeforeUpdate).isNull();
        softly.assertThat(nameBeforeUpdate).isNotEqualTo(actualName);
        softly.assertThat(expectedName).isEqualTo(actualName);

        var foundUser = AdminSteps.getUser(userRequest);

        UserDao userDao = DataBaseSteps.getUserByUsername(foundUser.getUsername());
        DaoAndModelAssertions.assertThat(foundUser, userDao).match();

    }

    public static Stream<Arguments> invalidData() {
        return Stream.of(
                Arguments.of("asdc","message", "Name must contain two words with letters only"),
                Arguments.of("asdc123123 acvxc","message", "Name must contain two words with letters only"),
                Arguments.of("123123 adsasd", "message", "Name must contain two words with letters only"),
                Arguments.of("ADASD #@$@#$@$#","message", "Name must contain two words with letters only"),
                Arguments.of("asdasd asdasd adasdasd","message", "Name must contain two words with letters only"),
                Arguments.of("adasdasd   ","message", "Name must contain two words with letters only"),
                Arguments.of("   adasdasd","message", "Name must contain two words with letters only"),
                Arguments.of("фвыфв фывфыв ", "message", "Name must contain two words with letters only")
        );
    }

    @MethodSource("invalidData")
    @ParameterizedTest(name = "Негативные тесты")
    public void negativeTest(String name,String errorKey, String error) {
        CreateUserRequest userRequest = AdminSteps.createUser();
        var user = new UserSteps(userRequest);

        UpdateCustomerProfileRequest updateCustomerProfileRequest = UpdateCustomerProfileRequest.builder()
                .name(name)
                .build();

        new CrudRequester
                (RequestSpecs.userSpec(userRequest.getUsername(), userRequest.getPassword()),
                        Endpoint.UPDATE_CUSTOMER_PROFILE,
                        ResponseSpecs.isBadRequest(errorKey, error))
                .put(updateCustomerProfileRequest);

        var nameAfterUpdate = user.getUserProfile().getName();

        softly.assertThat(nameAfterUpdate)
                .as("Имя не должно поменяться в API")
                .isNull();

        var foundUser = AdminSteps.getUser(userRequest);

        UserDao userDao = DataBaseSteps.getUserByUsername(foundUser.getUsername());
        softly.assertThat(userDao.getName())
                .as("Имя не должно поменяться в БД")
                .isNull();

    }


}
