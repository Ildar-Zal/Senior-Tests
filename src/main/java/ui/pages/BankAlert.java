package ui.pages;

import lombok.Getter;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Locale;

@Getter
public enum BankAlert {

    USER_CREATED_SUCCESSFULLY("✅ User created successfully!"),
    USERNAME_MUST_BE_BETWEEN_3_AND_15_CHARACTERS("Username must be between 3 and 15 characters"),
    NEW_ACCOUNT_CREATED("New Account Created! Account Number: "),
    NAME_UPDATED_SUCCESSFULLY("✅ Name updated successfully!"),
    SUCCESSFULLY_DEPOSITED("✅ Successfully deposited $%s to account %s!"),
    SUCCESSFULLY_TRANSFERED("✅ Successfully transferred $%s to account %s!"),
    NAME_MUST_CONTAIN_TWO_WORDS("Name must contain two words with letters only"),
    PLEASE_ENTER_VALID_NAME("❌ Please enter a valid name."),
    ERROR_INVALID_TRASFER("❌ Error: Invalid transfer: insufficient funds or invalid accounts"),
    NO_USER_FOUNT_WITH_ACCOUNT("❌ No user found with this account number."),
    PLEASE_ENTER_AMOUNT("❌ Please enter a valid amount."),
    PLEASE_FILL_ALL_FILED("❌ Please fill all fields and confirm."),
    PLEASE_SELECT_ACCOUNT("❌ Please select an account.");

    private final String message;

    BankAlert(String message) {
        this.message = message;
    }

    /**
     * Метод для динамического форматирования текста сообщения
     *
     * @param args переменные, которые нужно подставить вместо %s
     */
    public String format(Object... args) {
        Object[] formattedArgs = Arrays.stream(args)
                .map(arg -> {
                    // Если аргумент — число, форматируем его до 2 знаков с точкой
                    if (arg instanceof Double || arg instanceof Float || arg instanceof BigDecimal) {
                        return String.format(Locale.US, "%.2f", ((Number) arg).doubleValue());
                    }
                    return arg;
                })
                .toArray();

        return String.format(this.message, formattedArgs);
    }
}
