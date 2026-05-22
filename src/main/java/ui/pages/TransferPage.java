package ui.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;
import ui.elements.TransactionBage;

import java.util.List;

import static com.codeborne.selenide.Selenide.$;


public class TransferPage extends BasePage<TransferPage> {

    private ElementsCollection accounts = $(".form-control.account-selector").findAll("option");
    private SelenideElement makeTransferText = $(Selectors.byText("🔄 Make a Transfer"));
    private SelenideElement recipienNameInput = $(Selectors.byAttribute("placeholder", "Enter recipient name"));
    private SelenideElement recipientAccountNumberInput = $(Selectors.byAttribute("placeholder", "Enter recipient account number"));
    private SelenideElement amountInput = $(Selectors.byAttribute("placeholder", "Enter amount"));
    private SelenideElement checkBox = $("input[type='checkbox']");
    private SelenideElement sendTransferButton = $(Selectors.byText("🚀 Send Transfer"));
    private SelenideElement trasferAgainButton = $(Selectors.byText("🔁 Transfer Again"));


    @Override
    public String url() {
        return "/transfel";
    }

    @Override
    public TransferPage waitForLoadPage() {
        makeTransferText.shouldBe(Condition.visible);
        return this;
    }

    public TransferPage sendTransfer(String accountNumber, String recipientName, String recipientAccountNumber, String amount) {
        accounts.findBy(Condition.partialText(accountNumber)).click();
        recipienNameInput.setValue(recipientName);
        recipientAccountNumberInput.setValue(recipientAccountNumber);
        amountInput.setValue(amount);
        checkBox.click();
        sendTransferButton.click();
        return this;
    }

    public SelenideElement getSubmitButton() {
        return sendTransferButton;
    }

    public TransferPage clickTrasfetButton() {
        sendTransferButton.click();
        return this;
    }

    public TransactionInfoPage openTrasactionInfoPage() {
        trasferAgainButton.click();
        return new TransactionInfoPage().waitForLoadPage();
    }

}
