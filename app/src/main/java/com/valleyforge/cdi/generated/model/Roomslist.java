
package com.valleyforge.cdi.generated.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Roomslist {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("deleted_at")
    @Expose
    private Object deletedAt;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("room_name")
    @Expose
    private String roomName;
    @SerializedName("floor_plan_id")
    @Expose
    private String floorPlanId;
    @SerializedName("room_status")
    @Expose
    private String roomStatus;
    @SerializedName("no_of_windows")
    @Expose
    private String noOfWindows;
    @SerializedName("room_desc")
    @Expose
    private String roomDesc;
    @SerializedName("room_approval_status")
    @Expose
    private String roomApprovalStatus;
    @SerializedName("skip_reason")
    @Expose
    private String skipReason;
    @SerializedName("skip_flag")
    @Expose
    private String skipFlag;

    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getFloorPlanId() {
        return floorPlanId;
    }

    public void setFloorPlanId(String floorPlanId) {
        this.floorPlanId = floorPlanId;
    }

    public String getRoomStatus() {
        return roomStatus;
    }

    public void setRoomStatus(String roomStatus) {
        this.roomStatus = roomStatus;
    }

    public String getNoOfWindows() {
        return noOfWindows;
    }

    public void setNoOfWindows(String noOfWindows) {
        this.noOfWindows = noOfWindows;
    }

    public String getRoomDesc() {
        return roomDesc;
    }

    public void setRoomDesc(String roomDesc) {
        this.roomDesc = roomDesc;
    }

    public String getRoomApprovalStatus() {
        return roomApprovalStatus;
    }

    public void setRoomApprovalStatus(String roomApprovalStatus) {
        this.roomApprovalStatus = roomApprovalStatus;
    }

    public String getSkipReason() {
        return skipReason;
    }

    public void setSkipReason(String skipReason) {
        this.skipReason = skipReason;
    }

    public String getSkipFlag() {
        return skipFlag;
    }

    public void setSkipFlag(String skipFlag) {
        this.skipFlag = skipFlag;
    }


}
