package api.models.comparison;

import org.assertj.core.api.Assertions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ModelAssertions<T> {

    private final T actual;
    private final T expected;
    private final List<String> ignoredFields = new ArrayList<>();

    private ModelAssertions(T actual, T expected) {
        this.actual = actual;
        this.expected = expected;
    }

    // Статический метод для входа
    public static <T> ModelAssertions<T> assertThatModels(T actual, T expected) {
        return new ModelAssertions<>(actual, expected);
    }

    // Метод для накопления игнорируемых полей
    public ModelAssertions<T> ignoringFields(String... fields) {
        this.ignoredFields.addAll(Arrays.asList(fields));
        return this;
    }

    // Финальный метод сравнения
    public void match() {
        Assertions.assertThat(actual)
                .usingRecursiveComparison() // Сравниваем поля объектов рекурсивно
                .ignoringFields(ignoredFields.toArray(new String[0])) // Пропускаем ненужные
                .isEqualTo(expected);
    }
}