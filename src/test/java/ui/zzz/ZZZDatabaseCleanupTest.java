package ui.zzz;

import api.models.UserResponse;
import api.requests.skelethon.Endpoint;
import api.requests.skelethon.requesters.CrudRequester;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;
import org.junit.jupiter.api.Test;

import static api.specs.ResponseSpecs.anyStatus;
public class ZZZDatabaseCleanupTest {

    @Test
    public void tearDownBackendData() {
        System.out.println("[INFO] >>> Финальное удаление пользователей...");
        try {
            var allUsers = new CrudRequester(RequestSpecs.adminSpec(), Endpoint.ADMIN_USERS, anyStatus())
                    .get(null).extract().jsonPath().getList("", UserResponse.class);

            if (allUsers != null) {
                allUsers.forEach(user -> {
                    if ("admin".equals(user.getUsername())) {
                        return; // Не даем удалять админа!
                    }
                    try {
                        new CrudRequester(RequestSpecs.adminSpec(), Endpoint.ADMIN_USERS, ResponseSpecs.isOk())
                                .delete(user.getId());
                    } catch (Exception ignored) {}
                });
            }
        } catch (Exception e) {
            System.err.println("[WARN] Ошибка при очистке: " + e.getMessage());
        }
    }
}
