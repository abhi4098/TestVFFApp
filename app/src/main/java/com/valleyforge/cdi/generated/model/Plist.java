
package com.valleyforge.cdi.generated.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Plist {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("deleted_at")
    @Expose
    private Object deletedAt;
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
    private Integer propertyId;
    @SerializedName("assign_pm")
    @Expose
    private Integer assignPm;
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
    private Integer assignMt;
    @SerializedName("project_percentage")
    @Expose
    private Integer projectPercentage;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Object getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Object deletedAt) {
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

    public Integer getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(Integer propertyId) {
        this.propertyId = propertyId;
    }

    public Integer getAssignPm() {
        return assignPm;
    }

    public void setAssignPm(Integer assignPm) {
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

    public Integer getAssignMt() {
        return assignMt;
    }

    public void setAssignMt(Integer assignMt) {
        this.assignMt = assignMt;
    }

    public Integer getProjectPercentage() {
        return projectPercentage;
    }

    public void setProjectPercentage(Integer projectPercentage) {
        this.projectPercentage = projectPercentage;
    }

}
