package ui.iteration1;

import api.generators.RandomModelGenerator;
import api.models.CreateUserRequest;
import api.models.comparison.ModelAssertions;
import api.requests.steps.AdminSteps;
import common.annotations.AdminSession;
import org.junit.jupiter.api.Test;
import ui.BaseUiTest;
import ui.pages.AdminPanel;
import ui.pages.BankAlert;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CreateUserTest extends BaseUiTest {

    @AdminSession
    @Test
    public void adminCanCreateUserTest() {
        CreateUserRequest newUser = RandomModelGenerator.generate(CreateUserRequest.class);

       var userBage = new AdminPanel().open().createUser(newUser.getUsername(), newUser.getPassword())
                .checkAlertMessageAndAccept(BankAlert.USER_CREATED_SUCCESSFULLY.getMessage())
               .findUserByUsername(newUser.getUsername());

       assertThat(userBage)
               .as("UserBage should exist on Dashboard after user creation").isNotNull();

        var expectedUser = AdminSteps.getAllUsers().stream().filter(u -> u.getUsername().contains(newUser.getUsername())).findFirst().get();

       ModelAssertions.assertThatModels(newUser,expectedUser).match();




    }

    @AdminSession
    @Test
    public void adminCannotCreateUserWithInvalidData() {

        CreateUserRequest newUser = AdminSteps.createUser();
        newUser.setUsername("a");

        assertTrue(new AdminPanel().open().createUser(newUser.getUsername(),newUser.getPassword())
                .checkAlertMessageAndAccept(BankAlert.USERNAME_MUST_BE_BETWEEN_3_AND_15_CHARACTERS.getMessage())
                .getAllUsers().stream().noneMatch(userBage -> userBage.getUsername().equals(newUser.getUsername())));

        long usersWithSameUserNameAsNewUser = AdminSteps.getAllUsers().stream().filter(user1 -> user1.getUsername().equals(newUser.getUsername())).count();
        assertThat(usersWithSameUserNameAsNewUser).isZero();
    }
}
