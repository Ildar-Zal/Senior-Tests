package ui.pages;

import com.codeborne.selenide.*;
import common.utils.RetryUtils;
import lombok.Getter;
import org.openqa.selenium.Alert;
import org.openqa.selenium.Keys;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

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
        RetryUtils.retry(
                () -> {
                    try {
                        // 1. Избавляемся от возможных зависших алертов перед вводом
                        WebDriverWait shortWait = new WebDriverWait(WebDriverRunner.getWebDriver(), Duration.ofMillis(300));
                        Alert activeAlert = shortWait.until(ExpectedConditions.alertIsPresent());
                        activeAlert.accept();
                    } catch (Exception ignored) {}

                    // 2. Чистим поле до идеальной пустоты
                    enterNewNameInput.shouldBe(Condition.visible, Condition.enabled).clear();
                    if (!enterNewNameInput.getValue().isEmpty()) {
                        enterNewNameInput.sendKeys(Keys.CONTROL + "a", Keys.BACK_SPACE);
                    }

                    // 3. Вводим имя чистым Selenide (чтобы триггерить все события фронта)
                    enterNewNameInput.setValue(name);
                    enterNewNameInput.shouldHave(Condition.exactValue(name));

                    // 4. Сохраняем
                    saveChangesButton.shouldBe(Condition.visible, Condition.enabled).click();

                    // 5. Проверяем, что вылезло после клика
                    WebDriverWait wait = new WebDriverWait(WebDriverRunner.getWebDriver(), Duration.ofSeconds(2));
                    try {
                        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
                        String alertText = alert.getText();

                        if (alertText.contains("Please enter a valid name")) {
                            alert.accept(); // Закрываем плохой алерт сразу же!
                            throw new RuntimeException("Фронт выплюнул ошибку валидации. Идем на ретрай...");
                        }
                    } catch (org.openqa.selenium.TimeoutException e) {
                        // Если алерт не появился мгновенно — возможно, система думает, это нормально
                    }

                    return true;
                },
                result -> result != null && result,
                4,
                1200 // Чуть увеличим паузу, чтобы стейт успевал отдуплиться
        );

        return this;
    }

    public EditProfilePage clickSaveChanges() {
        saveChangesButton.click();
        return this;
    }

    public SelenideElement getSubmitButton() {
        return saveChangesButton;
    }
}
