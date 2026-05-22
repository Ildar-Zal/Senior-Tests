package ui.pages;

import api.models.CreateUserRequest;
import api.specs.RequestSpecs;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.Alert;
import ui.elements.BaseElement;

import java.time.Duration;
import java.util.List;
import java.util.function.Function;

import static com.codeborne.selenide.Selenide.*;
import static org.assertj.core.api.Assertions.assertThat;

public abstract class BasePage<T extends BasePage> {

    protected SelenideElement usernameInput = $(Selectors.byAttribute("placeholder", "Username"));
    protected SelenideElement passwordInput = $(Selectors.byAttribute("placeholder", "Password"));

    public abstract String url();

    public abstract T waitForLoadPage();

    public T open() {
        Selenide.open(url());
        return waitForLoadPage();
    }

    public <T extends BasePage> T getPage(Class<T> pageClass) {
        return Selenide.page(pageClass);
    }

    public T checkAlertMessageAndAccept(String bankAlert) {
        Alert alert = switchTo().alert();
        assertThat(alert.getText()).contains(bankAlert);
        alert.accept();
        return (T) this;
    }

    public T checkAlertMessageAndAccept(SelenideElement submitButton, String bankAlert) {
        // 1. Делаем клик по кнопке (на случай, если от Tab алерт не вылез)
        try {
            com.codeborne.selenide.Selenide.executeJavaScript("arguments[0].click();", submitButton);
        } catch (Exception ignored) {}

        // 2. Ждем и перехватываем самый первый алерт через встроенный механизм Selenide
        Alert alert = switchTo().alert();
        assertThat(alert.getText()).contains(bankAlert);
        alert.accept();

        // 3. ХАК ПРОТИВ ДУБЛИРУЮЩИХ АЛЕРТОВ (OnBlur + Клик):
        // Быстро проверяем чистым драйвером, не вылез ли под ним призрачный второй алерт.
        // Если вылез — уничтожаем его, чтобы он не вешал следующие страницы.
        while (true) {
            try {
                Alert ghostAlert = com.codeborne.selenide.WebDriverRunner.getWebDriver().switchTo().alert();
                System.out.println("[WARN] Обнаружен дублирующий алерт: " + ghostAlert.getText() + ". Закрываем...");
                ghostAlert.accept();
            } catch (org.openqa.selenium.NoAlertPresentException e) {
                // Как только алерты в Хроме закончились — выходим из цикла
                break;
            }
        }

        return (T) this;
    }

    public T checkAlertMessageAndAccept(BankAlert alert, Object... args) {
        String formattedMessage = alert.format(args);
        return checkAlertMessageAndAccept(formattedMessage);
    }

    public T checkAlertMessageAndAccept(SelenideElement submitButton, BankAlert alert, Object... args) {
        // 1. Форматируем сообщение по твоей логике из энама
        String formattedMessage = alert.format(args);

        // 2. Вызываем метод с ретраями клика, который мы написали выше
        return checkAlertMessageAndAccept(submitButton, formattedMessage);
    }

    public static void authAsUser(String username, String password) {
        Selenide.open("/");
        String userAuthHeader = RequestSpecs.getUserAuthHeader(username, password);
        executeJavaScript("localStorage.setItem('authToken', arguments[0])", userAuthHeader);
    }

    public static void authAsUser(CreateUserRequest createUserRequest) {
        authAsUser(createUserRequest.getUsername(), createUserRequest.getPassword());
    }

    // ElementCollection -> List<BaseElement>
    protected <T extends BaseElement> List<T> generatePageElements(ElementsCollection elementsCollection, Function<SelenideElement, T> constructor) {
        return elementsCollection.stream().map(constructor).toList();
    }
}
