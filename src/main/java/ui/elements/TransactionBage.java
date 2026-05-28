package ui.elements;

import com.codeborne.selenide.SelenideElement;
import lombok.Getter;

@Getter
public class TransactionBage extends BaseElement {
    private String type;
    private String amount;
    private String foundUnderUser;

    public TransactionBage(SelenideElement element) {
        super(element);
        String fullText = element.getText();

        String[] lines = fullText.split("\n");

        if (lines.length >= 1) {
            String[] firstLineParts = lines[0].split(" - ");
            this.type = firstLineParts[0].trim();
            if (firstLineParts.length > 1) {
                this.amount = firstLineParts[1].replace("$", "").trim();
            } else {
                this.amount = "";
            }
        }

        if (lines.length >= 2) {
            this.foundUnderUser = lines[1].replace("Found under: ", "").trim();
        }
    }


}
