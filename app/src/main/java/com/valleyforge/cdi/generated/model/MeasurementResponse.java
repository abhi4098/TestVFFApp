
package com.valleyforge.cdi.generated.model;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MeasurementResponse {

    @SerializedName("type")
    @Expose
    private Integer type;
    @SerializedName("token")
    @Expose
    private Integer token;
    @SerializedName("msg")
    @Expose
    private String msg;

    @SerializedName("submitted_for_review")
    @Expose
    private String submittedForReview;

    @SerializedName("measurement_details")
    @Expose
    private List<MeasurementDetail> measurementDetails = null;

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

    public String getSubmittedForReview() {
        return submittedForReview;
    }

    public void setSubmittedForReview(String submittedForReview) {
        this.submittedForReview = submittedForReview;
    }

    public List<MeasurementDetail> getMeasurementDetails() {
        return measurementDetails;
    }

    public void setMeasurementDetails(List<MeasurementDetail> measurementDetails) {
        this.measurementDetails = measurementDetails;
    }

}
