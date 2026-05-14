package requests.skelethon.requesters;

import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import models.BaseModel;
import requests.skelethon.Endpoint;
import requests.skelethon.HttpRequest;
import requests.skelethon.interfaces.Crud;

import java.util.List;

public class ValidatableCrudRequester<T extends BaseModel> extends HttpRequest implements Crud {

    private CrudRequester crudRequester;

    public ValidatableCrudRequester(RequestSpecification requestSpecification, Endpoint endpoint, ResponseSpecification responseSpecification) {
        super(requestSpecification, endpoint, responseSpecification);
        this.crudRequester = new CrudRequester(requestSpecification, endpoint, responseSpecification);
    }

    @Override
    public T post(BaseModel model) {
        return (T) crudRequester.post(model).extract().as(endpoint.getResponseModel());
    }

    @Override
    public T get(Object... pathParams) {
        return (T) crudRequester.get(pathParams).extract().as(endpoint.getResponseModel());
    }

    @Override
    public T put(BaseModel model, Object... pathParams) {
        return (T) crudRequester.put(model, pathParams).extract().as(endpoint.getResponseModel());
    }

    @Override
    public T delete(Integer id) {
        return (T) crudRequester.delete(id).extract().as(endpoint.getRequestModel());
    }

    @SuppressWarnings("unchecked")
    public List<T> getList(Object... pathParams) {
        return crudRequester.get(pathParams)
                .extract()
                .body()
                .jsonPath()
                .getList("", (Class<T>) endpoint.getResponseModel());
    }
}
