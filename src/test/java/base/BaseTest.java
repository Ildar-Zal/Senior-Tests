package base;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

public class BaseTest {
    protected SoftAssertions softly;

    @BeforeEach
    public void setUp() {
        this.softly = new SoftAssertions();
    }

    @AfterEach
    public void exit() {
        this.softly.assertAll();
    }

}
