package ui.pages;

import com.codeborne.selenide.*;
import common.utils.RetryUtils;
import lombok.Getter;
import ui.elements.UserBage;

import java.util.List;
import java.util.Objects;

import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;

@Getter
public class AdminPanel extends BasePage<AdminPanel> {
    private SelenideElement adminPanelText = $(Selectors.byText("Admin Panel"));
    private SelenideElement addUserButton = $(Selectors.byText("Add User"));

//    private ElementsCollection usersFromDashboard = $(byText("All users")).parent().findAll("li");

    @Override
    public String url() {
        return "/admin";
    }

    @Override
    public AdminPanel waitForLoadPage() {
        adminPanelText.shouldBe(Condition.visible);
        return this;
    }

    public AdminPanel createUser(String username, String password) {
        usernameInput.sendKeys(username);
        passwordInput.setValue(password);
        addUserButton.click();
        return this;
    }

    public List<UserBage> getAllUsers() {
        ElementsCollection elementsCollection = $(byText("All Users")).parent().findAll("li");
        return generatePageElements(elementsCollection, UserBage::new);
    }

    public UserBage findUserByUsername(String username) {
        return RetryUtils.retry(
                () -> getAllUsers().stream().filter(userBage -> userBage.getUsername().equals(username)).findAny().orElse(null),
                Objects::nonNull,
                3,
                1000
        );
    }
}
