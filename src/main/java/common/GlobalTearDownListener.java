//package support;
//
//import api.models.UserResponse;
//import api.requests.skelethon.Endpoint;
//import api.requests.skelethon.requesters.CrudRequester;
//import api.specs.RequestSpecs;
//import api.specs.ResponseSpecs;
//import org.junit.platform.launcher.LauncherSession;
//import org.junit.platform.launcher.LauncherSessionListener;
//
//public class GlobalTearDownListener implements LauncherSessionListener {
//
//    @Override
//    public void launcherSessionClosed(LauncherSession session) {
//        System.out.println("[INFO] >>> ВСЕ ТЕСТЫ ЗАВЕРШЕНЫ. Начинается финальное удаление пользователей...");
//        try {
//            // Твой код очистки
//            var allUsers = new CrudRequester(RequestSpecs.adminSpec(), Endpoint.ADMIN_USERS, ResponseSpecs.isOk())
//                    .get(null).extract().jsonPath().getList("", UserResponse.class);
//
//            if (allUsers != null) {
//                allUsers.forEach(user -> {
//                    try {
//                        new CrudRequester(RequestSpecs.adminSpec(), Endpoint.ADMIN_USERS, ResponseSpecs.isOk())
//                                .delete(user.getId());
//                    } catch (Exception ignored) {}
//                });
//            }
//            System.out.println("[INFO] >>> Очистка базы успешно завершена.");
//        } catch (Exception e) {
//            System.err.println("[WARN] Ошибка при глобальной очистке: " + e.getMessage());
//        }
//    }
//}