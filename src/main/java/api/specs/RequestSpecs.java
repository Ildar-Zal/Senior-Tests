package api.specs;

import api.config.Config;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import api.models.CreateUserRequest;
import api.requests.skelethon.Endpoint;
import api.requests.skelethon.requesters.CrudRequester;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestSpecs {

    private static final Map<String, String> authHeaders = new HashMap<>(Map.of("admin", "Basic YWRtaW46YWRtaW4="));

    private RequestSpecs() {
    }


    private static RequestSpecBuilder defaultSpec() {
        return new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .addFilters(List.of(new RequestLoggingFilter(),
                        new ResponseLoggingFilter()))
                .setBaseUri(Config.getProperty("apiBaseUrl") + Config.getProperty("apiVersion"));
    }

    public static RequestSpecification unauthSpec() {
        return defaultSpec().build();
    }

    public static RequestSpecification adminSpec() {
        return defaultSpec()
                .addHeader("Authorization", "Basic YWRtaW46YWRtaW4=")
                .build();
    }

    public static String getUserAuthHeader(String username, String password) {
        String authToken;
        if (!authHeaders.containsKey(username)) {
            authToken = new CrudRequester(RequestSpecs.unauthSpec(), Endpoint.AUTH_LOGIN, ResponseSpecs.isOk())
                    .post(CreateUserRequest.builder().username(username).password(password).build())
                    .extract().header("Authorization");
            authHeaders.put(username, authToken);
        } else {
            authToken = authHeaders.get(username);
        }
        return authToken;
    }

    public static RequestSpecification userSpec(String username, String password) {
        return defaultSpec().addHeader("Authorization", getUserAuthHeader(username,password)).build();
    }

}
