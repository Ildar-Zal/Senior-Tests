package specs;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;

import java.util.List;

public class RequestSpecs {
    private RequestSpecs(){}

    private static RequestSpecBuilder defaultSpec() {
       return new RequestSpecBuilder()
               .setContentType("application/json")
               .setAccept("application/json")
               .addFilters(List.of(new RequestLoggingFilter(),
                       new ResponseLoggingFilter()))
               .setBaseUri("http://localhost:4111");
    }

    public static RequestSpecification unauthSpec() {
        return defaultSpec().build();
    }
}
