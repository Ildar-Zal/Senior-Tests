package generators;

import org.apache.commons.lang3.RandomStringUtils;

public class RandomData {
    private RandomData(){}

    public static String randomUsername() {
        return RandomStringUtils.randomAlphabetic(10);
    }

    public static String randomPassword() {
        return RandomStringUtils.randomAlphabetic(3).toLowerCase()
                + RandomStringUtils.randomAlphabetic(3).toUpperCase()
                + RandomStringUtils.randomNumeric(5) + "%$!";
    }
}
