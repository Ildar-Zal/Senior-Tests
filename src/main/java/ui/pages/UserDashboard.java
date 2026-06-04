package ui.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;
import lombok.Getter;

import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;

@Getter
public class UserDashboard extends BasePage<UserDashboard> {
    private SelenideElement welcomeText = $(Selectors.byClassName("welcome-text"));
    private SelenideElement name = $(Selectors.byClassName("user-name"));
    private SelenideElement createNewAccount = $(byText("➕ Create New Account"));
    private SelenideElement depositAccount = $(byText("\uD83D\uDCB0 Deposit Money"));
    private SelenideElement makeTransfer = $(byText("🔄 Make a Transfer"));

    @Override
    public String url() {
        return "/dashboard";
    }

    @Override
    public UserDashboard waitForLoadPage() {
        welcomeText.shouldBe(Condition.visible);
        return this;
    }

    public UserDashboard createNewAccount() {
        createNewAccount.click();
        return this;
    }

    public EditProfilePage openEditProfilePage() {
        name.click();
        return new EditProfilePage().waitForLoadPage();
    }

    public DepositPage openDepositPage() {
        depositAccount.click();
        return new DepositPage().waitForLoadPage();
    }

    public TransferPage openTransferPage() {
        makeTransfer.click();
        return new TransferPage().waitForLoadPage();
    }

    public TransferPage testMethod() {
        return new TransferPage();//2345
    }

}
