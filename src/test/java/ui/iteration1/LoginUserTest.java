package ui.iteration1;

import com.codeborne.selenide.*;
import api.models.CreateUserRequest;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Alert;
import api.requests.steps.AdminSteps;
import ui.BaseUiTest;
import ui.pages.AdminPanel;
import ui.pages.LoginPage;
import ui.pages.UserDashboard;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.switchTo;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class LoginUserTest extends BaseUiTest {
    @Test
    public void adminCanLoginWithCorrectDataTest() {
        CreateUserRequest admin = CreateUserRequest.getAdmin();
        new LoginPage().open().login(admin.getUsername(), admin.getPassword())
                .getPage(AdminPanel.class).getAdminPanelText().shouldBe(Condition.visible);
    }

    @Test
    public void userCanLoginWithCorrectDataTest() {
        CreateUserRequest user = AdminSteps.createUser();
        new LoginPage().open().login(user.getUsername(), user.getPassword())
                .getPage(UserDashboard.class).getWelcomeText().shouldBe(Condition.visible)
                .shouldHave(Condition.text("Welcome, noname"));

    }

    @Test
    public void userCantLoginWithIvalidDataTest() {
        CreateUserRequest user = AdminSteps.createUser();
        Selenide.open("/login");
        $(Selectors.byAttribute("placeholder", "Username")).sendKeys("1234");
        $(Selectors.byAttribute("placeholder", "Password")).sendKeys(user.getPassword());
        $("button").click();
        Alert alert = switchTo().alert();
        assertEquals(alert.getText(), "Invalid credentialsAxiosError: Request failed with status code 401");
    }

}
