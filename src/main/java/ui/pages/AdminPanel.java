package ui.pages;

import com.codeborne.selenide.*;
import lombok.Getter;
import ui.elements.UserBage;

import java.util.List;

import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;

@Getter
public class AdminPanel extends BasePage<AdminPanel> {
    private SelenideElement adminPanelText = $(Selectors.byText("Admin Panel"));
    private SelenideElement addUserButton = $(Selectors.byText("Add User"));

    ElementsCollection usersFromDashboard = $(byText("All users")).parent().findAll("li");

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
        ElementsCollection elementsCollection = $(byText("All users")).parent().findAll("li");
        return generatePageElements(elementsCollection, UserBage::new);
    }
}
