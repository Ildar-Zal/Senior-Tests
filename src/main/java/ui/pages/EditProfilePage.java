package ui.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;
import lombok.Getter;

import static com.codeborne.selenide.Selenide.$;

@Getter
public class EditProfilePage extends BasePage<EditProfilePage> {

    private SelenideElement editProfileText = $(Selectors.byText("✏\uFE0F Edit Profile"));
    private SelenideElement enterNewNameInput = $(Selectors.byAttribute("placeholder", "Enter new name"));
    private SelenideElement saveChangesButton = $(Selectors.byText("\uD83D\uDCBE Save Changes"));


    @Override
    public String url() {
        return "/edit-profile";
    }

    @Override
    public EditProfilePage waitForLoadPage() {
        editProfileText.shouldBe(Condition.visible);
        return this;
    }

    public EditProfilePage enterNewName(String name) {
        enterNewNameInput.shouldBe(Condition.visible).shouldBe(Condition.enabled).setValue(name);
        enterNewNameInput.shouldHave(Condition.exactValue(name));
        saveChangesButton.shouldBe(Condition.visible).shouldBe(Condition.enabled).click();

        return this;
    }

    public EditProfilePage clickSaveChanges() {
        saveChangesButton.click();
        return this;
    }

    public  SelenideElement getSubmitButton() {
        return saveChangesButton;
    }
}
