
package com.valleyforge.cdi.generated.model;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ProjectDetailResponse {

    @SerializedName("type")
    @Expose
    private Integer type;
    @SerializedName("token")
    @Expose
    private Integer token;
    @SerializedName("msg")
    @Expose
    private String msg;
    @SerializedName("pdetail")
    @Expose
    private List<Pdetail> pdetail = null;

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

    public List<Pdetail> getPdetail() {
        return pdetail;
    }

    public void setPdetail(List<Pdetail> pdetail) {
        this.pdetail = pdetail;
    }

}
