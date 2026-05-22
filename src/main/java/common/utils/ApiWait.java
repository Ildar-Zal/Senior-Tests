package common.utils;

import java.util.List;
import java.util.function.Supplier;
public class ApiWait {

    /**
     * Супер-универсальный ожидальщик для ЛЮБЫХ одиночных объектов (Account, User и т.д.)
     * Крутится, пока объект равен null.
     */
    public static <T> T untilNotNull(Supplier<T> supplier) {
        int maxAttempts = 15;
        int delayMs = 300;

        for (int i = 0; i < maxAttempts; i++) {
            try {
                T result = supplier.get();
                if (result != null) {
                    return result; // Нашли объект — сразу возвращаем
                }
                Thread.sleep(delayMs);
            } catch (Exception ignored) {
                try { Thread.sleep(delayMs); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            }
        }
        return supplier.get(); // Если время истекло, возвращаем что есть (пусть падает ассерт, если там null)
    }

    public static <T> List<T> untilNotEmpty(Supplier<List<T>> listSupplier) {
        int maxAttempts = 15;
        int delayMs = 300;

        for (int i = 0; i < maxAttempts; i++) {
            try {
                List<T> list = listSupplier.get();
                // Проверяем и на null, и на пустоту!
                if (list != null && !list.isEmpty()) {
                    return list;
                }
                System.out.println("[ApiWait] Список пока пуст, ждем " + delayMs + "мс... (Попытка " + (i + 1) + ")");
                Thread.sleep(delayMs);
            } catch (Exception ignored) {
                try { Thread.sleep(delayMs); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            }
        }
        System.out.println("[WARN] ApiWait: Таймаут ожидания списка! Данные так и не появились.");
        return listSupplier.get();
    }
}
