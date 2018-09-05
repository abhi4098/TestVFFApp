
package com.valleyforge.cdi.generated.model;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SubmitWindowDetailResponse {

    @SerializedName("submitted_for_review")
    @Expose
    private Integer submittedForReview;
    @SerializedName("type")
    @Expose
    private Integer type;
    @SerializedName("token")
    @Expose
    private Integer token;
    @SerializedName("msg")
    @Expose
    private String msg;
    @SerializedName("measurement_details")
    @Expose
    private List<MeasurementDetail> measurementDetails = null;

    public Integer getSubmittedForReview() {
        return submittedForReview;
    }

    public void setSubmittedForReview(Integer submittedForReview) {
        this.submittedForReview = submittedForReview;
    }

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

    public List<MeasurementDetail> getMeasurementDetails() {
        return measurementDetails;
    }

    public void setMeasurementDetails(List<MeasurementDetail> measurementDetails) {
        this.measurementDetails = measurementDetails;
    }

}
