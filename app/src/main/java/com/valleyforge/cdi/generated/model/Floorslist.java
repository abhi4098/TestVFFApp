
package com.valleyforge.cdi.generated.model;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Floorslist {

    @SerializedName("id")
    @Expose
    private int id;

    @SerializedName("roomscount")
    @Expose
    private String roomscount;

    @SerializedName("completedroomscount")
    @Expose
    private String completedroomscount;

    @SerializedName("deleted_at")
    @Expose
    private String deletedAt;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("property_id")
    @Expose
    private String propertyId;
    @SerializedName("project_id")
    @Expose
    private String projectId;
    @SerializedName("floor_name")
    @Expose
    private String floor;
    @SerializedName("rooms")
    @Expose
    private List<Room> rooms = null;

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public String getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(String propertyId) {
        this.propertyId = propertyId;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getFloor() {
        return floor;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

    public List<Room> getRooms() {
        return rooms;
    }

    public void setRooms(List<Room> rooms) {
        this.rooms = rooms;
    }

    public String getRoomscount() {
        return roomscount;
    }

    public void setRoomscount(String roomscount) {
        this.roomscount = roomscount;
    }



    public String getCompletedroomscount() {
        return completedroomscount;
    }

    public void setCompletedroomscount(String completedroomscount) {
        this.completedroomscount = completedroomscount;
    }


}
