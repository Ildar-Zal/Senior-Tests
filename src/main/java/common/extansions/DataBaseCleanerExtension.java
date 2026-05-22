package common.extansions;

import api.models.UserResponse;
import api.requests.skelethon.Endpoint;
import api.requests.skelethon.requesters.CrudRequester;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;


public class DataBaseCleanerExtension implements BeforeAllCallback, ExtensionContext.Store.CloseableResource {

    private static boolean started = false;

    @Override
    public void beforeAll(ExtensionContext context) {
        if (!started) {
            started = true;
            // Логика перед стартом всех тестов (если нужна)
            // Регистрируем callback на закрытие всей тестовой сессии JUnit
            context.getRoot().getStore(ExtensionContext.Namespace.GLOBAL).put("DatabaseCleanerExtension", this);
        }
    }

    @Override
    public void close() {
        // ВОТ СЮДА пишем твое удаление! Оно выполнится строго 1 раз, когда JUnit закроет ВСЕ тесты.
        System.out.println("[INFO] Запуск глобальной очистки пользователей после ВСЕХ тестов...");
        try {
            var allUsers = new CrudRequester(RequestSpecs.adminSpec(), Endpoint.ADMIN_USERS, ResponseSpecs.isOk())
                    .get(null).extract().jsonPath().getList("", UserResponse.class);

            if (allUsers != null) {
                allUsers.forEach(user -> {
                    try {
                        new CrudRequester(RequestSpecs.adminSpec(), Endpoint.ADMIN_USERS, ResponseSpecs.isOk())
                                .delete(user.getId());
                    } catch (Exception ignored) {
                    }
                });
            }
        } catch (Exception e) {
            System.err.println("[ERROR] Ошибка глобальной очистки: " + e.getMessage());
        }
    }
}
