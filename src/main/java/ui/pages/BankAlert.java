package ui.pages;

import lombok.Getter;

@Getter
public enum BankAlert {

    USER_CREATED_SUCCESSFULLY("✅ User created successfully!"),
    USERNAME_MUST_BE_BETWEEN_3_AND_15_CHARACTERS("Username must be between 3 and 15 characters"),
    NEW_ACCOUNT_CREATED("New Account Created! Account Number: "),
    NAME_UPDATED_SUCCESSFULLY("✅ Name updated successfully!"),
    NAME_MUST_CONTAIN_TWO_WORDS_WITH_LETTERS_ONLY("Name must contain two words with letters only"),
    PLEASE_ENTER_VALID_NAME("❌ Please enter a valid name."),
    SUCCESSFULLY_DEPOSITED("✅ Successfully deposited $%s to account %s!"),
    PLEASE_ENTER_AMOUNT("❌ Please enter a valid amount."),
    PLEASE_SELECT_ACCOUNT("❌ Please select an account.");

    private final String message;

    BankAlert(String message) {
        this.message = message;
    }

    /**
     * Метод для динамического форматирования текста сообщения
     * @param args переменные, которые нужно подставить вместо %s
     */
    public String format(Object... args) {
        return String.format(this.message, args);
    }
}
