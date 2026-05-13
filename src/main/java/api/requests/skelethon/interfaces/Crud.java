package api.requests.skelethon.interfaces;

import api.models.BaseModel;

public interface Crud {

    public Object post(BaseModel model);

    public Object get(Integer id);

    public Object put(Integer id, BaseModel model);

    public Object delete(Integer id);
}
