
package com.valleyforge.cdi.generated.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DashboardDataResponse {

    @SerializedName("type")
    @Expose
    private Integer type;
    @SerializedName("token")
    @Expose
    private Integer token;
    @SerializedName("msg")
    @Expose
    private String msg;
    @SerializedName("pendingprojects")
    @Expose
    private Integer pendingprojects;
    @SerializedName("inprogressprojects")
    @Expose
    private Integer inprogressprojects;
    @SerializedName("completedprojects")
    @Expose
    private Integer completedprojects;

    @SerializedName("submittedforreviewprojects")
    @Expose
    private Integer submittedforreviewprojects;

    @SerializedName("submitforeditingcount")
    @Expose
    private Integer submitforeditingcount;


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

    public Integer getPendingprojects() {
        return pendingprojects;
    }

    public void setPendingprojects(Integer pendingprojects) {
        this.pendingprojects = pendingprojects;
    }

    public Integer getInprogressprojects() {
        return inprogressprojects;
    }

    public void setInprogressprojects(Integer inprogressprojects) {
        this.inprogressprojects = inprogressprojects;
    }

    public Integer getCompletedprojects() {
        return completedprojects;
    }

    public void setCompletedprojects(Integer completedprojects) {
        this.completedprojects = completedprojects;
    }

    public Integer getSubmittedforreviewprojects() {
        return submittedforreviewprojects;
    }

    public void setSubmittedforreviewprojects(Integer submittedforreviewprojects) {
        this.submittedforreviewprojects = submittedforreviewprojects;
    }


    public Integer getSubmitforeditingcount() {
        return submitforeditingcount;
    }

    public void setSubmitforeditingcount(Integer submitforeditingcount) {
        this.submitforeditingcount = submitforeditingcount;
    }

}
