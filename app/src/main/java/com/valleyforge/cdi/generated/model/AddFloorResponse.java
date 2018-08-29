
package com.valleyforge.cdi.generated.model;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AddFloorResponse {

    @SerializedName("type")
    @Expose
    private Integer type;
    @SerializedName("token")
    @Expose
    private Integer token;
    @SerializedName("msg")
    @Expose
    private String msg;
    @SerializedName("floorplandetails")
    @Expose
    private List<Floorplandetail> floorplandetails = null;

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getToken() {
        return token;
    }

    public void setToken(Integer token) {
        this.token = token;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<Floorplandetail> getFloorplandetails() {
        return floorplandetails;
    }

    public void setFloorplandetails(List<Floorplandetail> floorplandetails) {
        this.floorplandetails = floorplandetails;
    }

}
