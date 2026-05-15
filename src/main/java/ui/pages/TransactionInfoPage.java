package ui.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;
import ui.elements.TransactionBage;

import java.util.List;

import static com.codeborne.selenide.Selenide.$;

public class TransactionInfoPage extends BasePage<TransactionInfoPage> {

    private SelenideElement matchingTransactionsText = $(Selectors.byText("Matching Transactions"));
    private SelenideElement usernameInput = $(Selectors.byAttribute("placeholder", "Enter name to find transactions"));
    private SelenideElement searchTransactionButton = $(Selectors.byText("🔍 Search Transactions"));

    @Override
    public String url() {
        return "";
    }

    @Override
    public TransactionInfoPage waitForLoadPage() {
        matchingTransactionsText.shouldBe(Condition.visible);
        return new TransactionInfoPage();
    }

    public TransactionInfoPage searchTransactions(String username) {
        matchingTransactionsText.shouldBe(Condition.visible);
        usernameInput.setValue(username);
        searchTransactionButton.click();
        return this;
    }

    public List<TransactionBage> getAllTransactions() {
        ElementsCollection elementsCollection = $(By.className("list-group")).findAll("li");
        return generatePageElements(elementsCollection, TransactionBage::new);
    }


}
