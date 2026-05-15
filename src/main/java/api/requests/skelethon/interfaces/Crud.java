package api.requests.skelethon.interfaces;

import api.models.BaseModel;

public interface Crud {

    public Object post(BaseModel model);

    public Object get(Object... pathParams);

    public Object put(BaseModel model, Object... pathParams);

    public Object delete(Integer id);
}
