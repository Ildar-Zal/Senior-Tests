package iteration1.ui;

import com.codeborne.selenide.*;
import api.generators.RandomModelGenerator;
import api.models.CreateUserRequest;
import api.models.comparison.ModelAssertions;
import common.annotations.AdminSession;
import org.junit.jupiter.api.Test;
import api.requests.steps.AdminSteps;
import ui.pages.AdminPanel;
import ui.pages.BankAlert;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CreateUserTest extends BaseUiTest {


    @Test
    @AdminSession
    public void adminCanCreateUserTest() {
//        CreateUserRequest admin = CreateUserRequest.getAdmin();
//
//        authAsUser(admin);


        CreateUserRequest newUser = RandomModelGenerator.generate(CreateUserRequest.class);

       assertTrue(new AdminPanel().open().createUser(newUser.getUsername(), newUser.getPassword())
                .checkAlertMessageAndAccept(BankAlert.USER_CREATED_SUCCESSFULLY.getMessage())
                .getAllUsers().stream().anyMatch(userBage -> userBage.getUsername().equals(newUser.getUsername())));
//                .getUsersFromDashboard().findBy(Condition.exactText(newUser.getUsername() + "\nUSER")).shouldBe(Condition.visible);

        var expectedUser = AdminSteps.getAllUsers().stream().filter(u -> u.getUsername().contains(newUser.getUsername())).findFirst().get();

       ModelAssertions.assertThatModels(expectedUser,newUser).match();


//        CreateUserRequest admin = CreateUserRequest.builder().username("admin").password("admin").build();
//
//        Selenide.open("/login");
//        $(Selectors.byAttribute("placeholder", "Username")).sendKeys(admin.getUsername());
//        $(Selectors.byAttribute("placeholder", "Password")).sendKeys(admin.getPassword());
//        $("button").click();
//
//        $(Selectors.byText("Admin Panel")).shouldBe(Condition.visible);
//
//        CreateUserRequest newUser = AdminSteps.createUser();
//
//        $(Selectors.byAttribute("placeholder", "Username")).sendKeys(newUser.getUsername());
//        $(Selectors.byAttribute("placeholder", "Password")).sendKeys(newUser.getPassword());
//
//        $(Selectors.byText("Add User")).click();
//
//        Alert alert = switchTo().alert();
//
//        assertEquals(alert.getText(), "✅ User created successfully!");
//
//        alert.accept();
//
//        //Шаг 4: проверка, что юзер отображается на UI
//
//        ElementsCollection allUsersFromDashboard = $(Selectors.byText("All Users")).parent().findAll("li");
//        allUsersFromDashboard.findBy(Condition.exactText(newUser.getUsername() + "\nUSER")).shouldBe(Condition.visible);
//
//        UserResponse[] users = given()
//                .spec(RequestSpecs.adminSpec())
//                .get("http://localhost:4111/v1/admin/users")
//                .then()
//                .assertThat()
//                .statusCode(HttpStatus.SC_OK)
//                .extract().as(UserResponse[].class);
//
//        UserResponse createdUser = Arrays.stream(users).filter(user1 -> user1.getUsername().equals(newUser.getUsername())).findFirst().get();
//
//        ModelAssertions.assertThatModels(createdUser, newUser).match();
//        //Шаг 5: проверка, что юзер отображается на API

    }

    @Test
    public void adminCannotCreateUserWithInvalidData() {

        CreateUserRequest newUser = AdminSteps.createUser();
        newUser.setUsername("a");

        assertTrue(new AdminPanel().open().createUser(newUser.getUsername(),newUser.getPassword())
                .checkAlertMessageAndAccept(BankAlert.USERNAME_MUST_BE_BETWEEN_3_AND_15_CHARACTERS.getMessage())
                .getAllUsers().stream().noneMatch(userBage -> userBage.getUsername().equals(newUser.getUsername())));
//                .getUsersFromDashboard().findBy(Condition.exactText(newUser.getUsername() + "\nUSER")).shouldNot(Condition.exist);

        long usersWithSameUserNameAsNewUser = AdminSteps.getAllUsers().stream().filter(user1 -> user1.getUsername().equals(newUser.getUsername())).count();
        assertThat(usersWithSameUserNameAsNewUser).isZero();
    }
}
