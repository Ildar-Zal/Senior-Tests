package ui.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;

public class LoginPage extends BasePage<LoginPage> {

    private SelenideElement button = $("button");
    private SelenideElement loginText = $(Selectors.byText("Login"));

    public LoginPage login(String username, String password) {
        usernameInput.setValue(username);
        passwordInput.setValue(password);
        button.click();
        return this;
    }

    @Override
    public String url() {
        return "/login";
    }

    @Override
    public LoginPage waitForLoadPage() {
        loginText.shouldBe(Condition.visible);
        return this;
    }
}
