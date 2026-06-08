package ui.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;
import lombok.Getter;

import static com.codeborne.selenide.Selenide.$;

@Getter
public class DepositPage extends BasePage<DepositPage> {

    ElementsCollection accounts = $(".form-control.account-selector").findAll("option");
    SelenideElement depositMoneyText = $(Selectors.byText("💰 Deposit Money"));
    SelenideElement enterAmountInput = $(Selectors.byAttribute("placeholder", "Enter amount"));
    SelenideElement depositButton = $(Selectors.byText("💵 Deposit"));

    @Override
    public String url() {
        return "/deposit";
    }

    @Override
    public DepositPage waitForLoadPage() {
        depositMoneyText.shouldBe(Condition.visible);
        return this;
    }

    public DepositPage depositMoney(String account, String amount) {
        accounts.findBy(Condition.partialText(account)).click();
        enterAmountInput.setValue(amount);
        depositButton.click();
        return this;
    }

    public DepositPage selectAccount(String account) {
        accounts.findBy(Condition.partialText(account)).click();
        return this;
    }

    public DepositPage clickDeposit() {
        depositButton.click();
        return this;
    }

}

