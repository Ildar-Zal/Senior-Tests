package api.dao.comparison;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class DaoComparator {

    private final DaoComparisonConfigLoader configLoader;

    public DaoComparator() {
        this.configLoader = new DaoComparisonConfigLoader("dao-comparison.properties");
    }

    public void compare(Object apiResponse, Object dao) {
        if (apiResponse == null && dao == null) return;
        if (apiResponse == null || dao == null) {
            throw new AssertionError(String.format("One of the objects is null. API: %s, DAO: %s", apiResponse, dao));
        }

        // Если нам на вход прилетели списки напрямую, сравниваем их как списки
        if (apiResponse instanceof List && dao instanceof List) {
            compareLists((List<?>) apiResponse, (List<?>) dao);
            return;
        }

        DaoComparisonConfigLoader.DaoComparisonRule rule = configLoader.getRuleFor(apiResponse.getClass());

        if (rule == null) {
            throw new RuntimeException("No comparison rule found for " + apiResponse.getClass().getSimpleName());
        }

        Map<String, String> fieldMappings = rule.getFieldMappings();

        for (Map.Entry<String, String> mapping : fieldMappings.entrySet()) {
            String apiFieldName = mapping.getKey();
            String daoFieldName = mapping.getValue();

            Object apiValue = getFieldValue(apiResponse, apiFieldName);
            Object daoValue = getFieldValue(dao, daoFieldName);

            // 1. ОБРАБОТКА СПИСКОВ: Если внутри полей лежат списки (например, транзакции)
            if (apiValue instanceof List && daoValue instanceof List) {
                try {
                    compareLists((List<?>) apiValue, (List<?>) daoValue);
                } catch (AssertionError e) {
                    throw new AssertionError(String.format("Mismatch inside list field '%s': %s", apiFieldName, e.getMessage()));
                }
                continue; // Списки успешно проверены, переходим к следующему полю
            }

            boolean isMatch;

            // 2. ОБРАБОТКА ЧИСЕЛ: Наша старая бронебойная проверка BigDecimal/Double
            if (apiValue instanceof Number && daoValue instanceof Number) {
                BigDecimal num1 = new BigDecimal(apiValue.toString());
                BigDecimal num2 = new BigDecimal(daoValue.toString());
                isMatch = num1.compareTo(num2) == 0;
            } else {
                // Стандартная логика для строк и простых объектов
                isMatch = Objects.equals(apiValue, daoValue);
            }

            if (!isMatch) {
                throw new AssertionError(String.format(
                        "Field mismatch for %s: API=%s, DAO=%s",
                        apiFieldName, apiValue, daoValue));
            }
        }
    }

    // Новый вспомогательный метод для рекурсивного сравнения списков объектов
    private void compareLists(List<?> apiList, List<?> daoList) {
        if (apiList.size() != daoList.size()) {
            throw new AssertionError(String.format("List size mismatch: API size=%d, DAO size=%d", apiList.size(), daoList.size()));
        }

        // Копируем списки, чтобы случайно не испортить оригинальные данные в тесте
        List<Object> sortedApi = new ArrayList<>(apiList);
        List<Object> sortedDao = new ArrayList<>(daoList);

        // Сортируем оба списка по значению их полей (например, по id или по amount),
        // чтобы элементы совпали по индексам, даже если бэк и БД вернули их в разном порядке
        sortedApi.sort(Comparator.comparing(obj -> getAnyFieldValue(obj, "id", "amount").toString()));
        sortedDao.sort(Comparator.comparing(obj -> getAnyFieldValue(obj, "id", "amount").toString()));

        // Поэлементно сравниваем отсортированные списки
        for (int i = 0; i < sortedApi.size(); i++) {
            compare(sortedApi.get(i), sortedDao.get(i));
        }
    }

    // Вспомогательный метод, который пытается достать хоть какое-то поле для сортировки (id или amount)
    private Object getAnyFieldValue(Object obj, String... fieldNames) {
        for (String name : fieldNames) {
            try {
                return getFieldValue(obj, name);
            } catch (Exception ignored) {
            }
        }
        return obj.hashCode(); // Хелбэк, если полей для сортировки не нашлось
    }

    private Object getFieldValue(Object obj, String fieldName) {
        Class<?> clazz = obj.getClass();
        while (clazz != null) {
            try {
                Field field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);
                return field.get(obj);
            } catch (NoSuchFieldException e) {
                clazz = clazz.getSuperclass();
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Failed to get field value: " + fieldName, e);
            }
        }
        throw new RuntimeException("Field not found: " + fieldName + " in class " + obj.getClass().getName());
    }

    private static class Objects {
        public static boolean equals(Object a, Object b) {
            return (a == b) || (a != null && a.equals(b));
        }
    }
}