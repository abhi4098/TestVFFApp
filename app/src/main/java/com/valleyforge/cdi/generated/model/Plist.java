
package com.valleyforge.cdi.generated.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Plist {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("deleted_at")
    @Expose
    private String deletedAt;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("pname")
    @Expose
    private String pname;
    @SerializedName("property_id")
    @Expose
    private String propertyId;
    @SerializedName("assign_pm")
    @Expose
    private String assignPm;
    @SerializedName("start_date")
    @Expose
    private String startDate;
    @SerializedName("end_date")
    @Expose
    private String endDate;
    @SerializedName("project_status")
    @Expose
    private String projectStatus;
    @SerializedName("project_id")
    @Expose
    private String projectId;
    @SerializedName("assign_mt")
    @Expose
    private String assignMt;
    @SerializedName("project_percentage")
    @Expose
    private String projectPercentage;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(String deletedAt) {
        this.deletedAt = deletedAt;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getPname() {
        return pname;
    }

    public void setPname(String pname) {
        this.pname = pname;
    }

    public String getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(String propertyId) {
        this.propertyId = propertyId;
    }

    public String getAssignPm() {
        return assignPm;
    }

    public void setAssignPm(String assignPm) {
        this.assignPm = assignPm;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getProjectStatus() {
        return projectStatus;
    }

    public void setProjectStatus(String projectStatus) {
        this.projectStatus = projectStatus;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getAssignMt() {
        return assignMt;
    }

    public void setAssignMt(String assignMt) {
        this.assignMt = assignMt;
    }

    public String getProjectPercentage() {
        return projectPercentage;
    }

    public void setProjectPercentage(String projectPercentage) {
        this.projectPercentage = projectPercentage;
    }

}
