package common.utils;

public interface Validatable {
    /**
     * Каждая DTO-модель сама решает, когда её данные считаются полностью готовыми.
     */
    boolean isValid();
}