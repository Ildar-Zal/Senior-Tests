package base;

import api.models.UserResponse;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import api.requests.skelethon.Endpoint;
import api.requests.skelethon.requesters.CrudRequester;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;

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

    @AfterAll()
    public static void delete() {
        var allUsers = new CrudRequester(RequestSpecs.adminSpec(), Endpoint.ADMIN_USERS, ResponseSpecs.isOk())
                .get(null).extract().jsonPath().getList("", UserResponse.class);

        allUsers.forEach(d -> {
            new CrudRequester(RequestSpecs.adminSpec(), Endpoint.ADMIN_USERS, ResponseSpecs.isOk())
                    .delete(d.getId());
        });
    }
}
