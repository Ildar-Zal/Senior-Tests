package specs;

import config.Config;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import models.CreateUserRequest;
import requests.CreateUserRequester;
import requests.LoginUserRequester;
import requests.skelethon.Endpoint;
import requests.skelethon.requesters.CrudRequester;
import requests.skelethon.requesters.ValidatableCrudRequester;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestSpecs {

    private static final Map<String, String> authHeaders = new HashMap<>(Map.of("admin", "Basic YWRtaW46YWRtaW4="));

    private RequestSpecs() { }

    private static RequestSpecBuilder defaultSpec() {
        return new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .addFilters(List.of(new RequestLoggingFilter(),
                        new ResponseLoggingFilter()))
                .setBaseUri(Config.getProperty("server") + Config.getProperty("apiVersion"));
    }

    public static RequestSpecification unauthSpec() {
        return defaultSpec().build();
    }

    public static RequestSpecification adminSpec() {
        return defaultSpec()
                .addHeader("Authorization", "Basic YWRtaW46YWRtaW4=")
                .build();
    }

    public static RequestSpecification userSpec(String username, String password) {
        if (!authHeaders.containsKey(username)) {
            String token = new CrudRequester(RequestSpecs.unauthSpec(), Endpoint.AUTH_LOGIN, ResponseSpecs.isOk())
                    .post(CreateUserRequest.builder().username(username).password(password).build())
                    .extract().header("Authorization");
            authHeaders.put(username, token);
        }

        return defaultSpec().addHeader("Authorization", authHeaders.get(username)).build();
    }
}
