package ui.iteration2;

import api.generators.RandomModelGenerator;
import api.models.UpdateCustomerProfileRequest;
import api.requests.steps.UserSteps;
import common.context.SessionStorage;
import common.annotations.UserSession;
import common.utils.ApiWait;
import org.junit.jupiter.api.Test;
import ui.BaseUiTest;
import ui.pages.BankAlert;
import ui.pages.EditProfilePage;
import ui.pages.UserDashboard;

import static com.codeborne.selenide.Selenide.switchTo;
import static org.assertj.core.api.Assertions.assertThat;

public class UpdateUserNameTest extends BaseUiTest {

    @UserSession
    @Test
    public void userCanChangeNameTest() throws InterruptedException {
        var user = new UserSteps(SessionStorage.getUser());
        var newName = RandomModelGenerator.generate(UpdateCustomerProfileRequest.class).getName();

        var editProfilePage = new UserDashboard().open().openEditProfilePage();
//        Thread.sleep(3000);
        editProfilePage.enterNewName(newName)
                .checkAlertMessageAndAccept(BankAlert.NAME_UPDATED_SUCCESSFULLY.getMessage());

        var actualName = new UserDashboard().open().getName().text();
        var expectedName = ApiWait.untilNotNull(() -> user.getUserProfile().getName());

        assertThat(expectedName).isEqualTo(actualName);
    }

    @UserSession
    @Test
    public void userCantChangeNameWithOneWordTest() {
        var user = new UserSteps(SessionStorage.getUser());

        new EditProfilePage().open().enterNewName("OneWord");
        String actualText = switchTo().alert().getText();

        softly.assertThat(actualText)
                .containsAnyOf(
                        BankAlert.NAME_MUST_CONTAIN_TWO_WORDS.getMessage(),
                        BankAlert.PLEASE_ENTER_VALID_NAME.getMessage()
                );

        switchTo().alert().accept();
        var actualName = new UserDashboard().open().getName().text();

        var expectedName = user.getUserProfile().getName();

        softly.assertThat(expectedName).isNull();
        softly.assertThat(actualName).isNotEqualTo(expectedName);
    }

    @UserSession
    @Test
    public void userCantChangeNameWithoutEnterNameTest() {
        var user = new UserSteps(SessionStorage.getUser());

        new EditProfilePage().open().clickSaveChanges()
                .checkAlertMessageAndAccept(BankAlert.PLEASE_ENTER_VALID_NAME.getMessage());
        var actualName = new UserDashboard().open().getName().text();

        var expectedName = user.getUserProfile().getName();

        softly.assertThat(expectedName).isNull();
        softly.assertThat(actualName).isNotEqualTo(expectedName);
    }
}
