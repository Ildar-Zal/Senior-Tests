package specs;

import io.restassured.builder.ResponseBuilder;
import org.apache.http.client.methods.RequestBuilder;

public class ResponseSpecs {
    private ResponseSpecs(){}

    private ResponseBuilder defaultSpec() {
        return new ResponseBuilder();
    }
}
