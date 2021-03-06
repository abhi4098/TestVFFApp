
package com.valleyforge.cdi.generated.model;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FloorDetailsResponse {

    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("token")
    @Expose
    private String token;
    @SerializedName("msg")
    @Expose
    private String msg;
    @SerializedName("floorslist")
    @Expose
    private List<Floorslist> floorslist = null;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<Floorslist> getFloorslist() {
        return floorslist;
    }

    public void setFloorslist(List<Floorslist> floorslist) {
        this.floorslist = floorslist;
    }

}
