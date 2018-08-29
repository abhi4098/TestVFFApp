package com.valleyforge.cdi.generated.tables;

import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Table(name = "PDetailTable")
public class PDetailTable {

    @Column(name="projectPercentage")
    private String id;
    @SerializedName("projectdeleted_at")
    @Expose
    private String projectdeletedAt;
    @SerializedName("projectcreated_at")
    @Expose
    private String projectcreatedAt;
    @SerializedName("projectupdated_at")
    @Expose
    private String projectupdatedAt;
    @SerializedName("projectname")
    @Expose
    private String projectname;
    @SerializedName("project_property_id")
    @Expose
    private String projectPropertyId;
    @SerializedName("project_assigned_pm")
    @Expose
    private String projectAssignedPm;
    @SerializedName("project_start_date")
    @Expose
    private String projectStartDate;
    @SerializedName("project_end_date")
    @Expose
    private String projectEndDate;
    @SerializedName("project_status")
    @Expose
    private String projectStatus;
    @SerializedName("project_id")
    @Expose
    private String projectId;
    @SerializedName("project_assign_mt")
    @Expose
    private String projectAssignMt;
    @SerializedName("project_percentage")
    @Expose
    private String projectPercentage;
    @SerializedName("properties_id")
    @Expose
    private String propertiesId;
    @SerializedName("properties_deleted_at")
    @Expose
    private String propertiesDeletedAt;
    @SerializedName("properties_created_at")
    @Expose
    private String propertiesCreatedAt;
    @SerializedName("properties_updated_at")
    @Expose
    private String propertiesUpdatedAt;
    @SerializedName("properties_name")
    @Expose
    private String propertiesName;
    @SerializedName("properties_email")
    @Expose
    private String propertiesEmail;
    @SerializedName("properties_phone")
    @Expose
    private String propertiesPhone;
    @SerializedName("properties_website")
    @Expose
    private String propertiesWebsite;
    @SerializedName("properties_assigned_to")
    @Expose
    private String propertiesAssignedTo;
    @SerializedName("properties_contact_person_name")
    @Expose
    private String propertiesContactPersonName;


    @SerializedName("properties_contact_person_phone")
    @Expose
    private String propertiesContactPersonPhone;

    @SerializedName("properties_address")
    @Expose
    private String propertiesAddress;

    @SerializedName("properties_city")
    @Expose
    private String propertiesCity;
    @SerializedName("properties_description")
    @Expose
    private String propertiesDescription;
    @SerializedName("properties_display_picture")
    @Expose
    private String propertiesDisplayPicture;
    @SerializedName("project_manager_name")
    @Expose
    private String projectManagerName;

    @SerializedName("project_appointment_date")
    @Expose
    private String appointmentDate;
}
