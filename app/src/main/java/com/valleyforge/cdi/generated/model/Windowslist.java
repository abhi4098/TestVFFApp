
package com.valleyforge.cdi.generated.model;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Windowslist {

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
    @SerializedName("floor_plan_id")
    @Expose
    private String floorPlanId;
    @SerializedName("floor_room")
    @Expose
    private String floorRoom;
    @SerializedName("window")
    @Expose
    private String window;
    @SerializedName("wall_width")
    @Expose
    private String wallWidth;
    @SerializedName("width_left_window")
    @Expose
    private String widthLeftWindow;
    @SerializedName("ib_width_window")
    @Expose
    private String ibWidthWindow;
    @SerializedName("ib_length_window")
    @Expose
    private String ibLengthWindow;
    @SerializedName("width_right_window")
    @Expose
    private String widthRightWindow;
    @SerializedName("length_ceil_flr")
    @Expose
    private String lengthCeilFlr;
    @SerializedName("pocket_depth")
    @Expose
    private String pocketDepth;
    @SerializedName("carpet_inst")
    @Expose
    private String carpetInst;
    @SerializedName("window_treatment")
    @Expose
    private String windowTreatment;
    @SerializedName("images")
    @Expose
    private String images;
    @SerializedName("property_id")
    @Expose
    private String propertyId;
    @SerializedName("project_id")
    @Expose
    private String projectId;
    @SerializedName("ceiling_to_floor")
    @Expose
    private String ceilingToFloor;
    @SerializedName("wall_to_wall")
    @Expose
    private String wallToWall;
    @SerializedName("image_type")
    @Expose
    private String imageType;
    @SerializedName("window_approval")
    @Expose
    private String windowApproval;
    @SerializedName("window_status")
    @Expose
    private String windowStatus;
    @SerializedName("allimages")
    @Expose
    private List<Allimage> allimages = null;

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

    public String getFloorPlanId() {
        return floorPlanId;
    }

    public void setFloorPlanId(String floorPlanId) {
        this.floorPlanId = floorPlanId;
    }

    public String getFloorRoom() {
        return floorRoom;
    }

    public void setFloorRoom(String floorRoom) {
        this.floorRoom = floorRoom;
    }

    public String getWindow() {
        return window;
    }

    public void setWindow(String window) {
        this.window = window;
    }

    public String getWallWidth() {
        return wallWidth;
    }

    public void setWallWidth(String wallWidth) {
        this.wallWidth = wallWidth;
    }

    public String getWidthLeftWindow() {
        return widthLeftWindow;
    }

    public void setWidthLeftWindow(String widthLeftWindow) {
        this.widthLeftWindow = widthLeftWindow;
    }

    public String getIbWidthWindow() {
        return ibWidthWindow;
    }

    public void setIbWidthWindow(String ibWidthWindow) {
        this.ibWidthWindow = ibWidthWindow;
    }

    public String getIbLengthWindow() {
        return ibLengthWindow;
    }

    public void setIbLengthWindow(String ibLengthWindow) {
        this.ibLengthWindow = ibLengthWindow;
    }

    public String getWidthRightWindow() {
        return widthRightWindow;
    }

    public void setWidthRightWindow(String widthRightWindow) {
        this.widthRightWindow = widthRightWindow;
    }

    public String getLengthCeilFlr() {
        return lengthCeilFlr;
    }

    public void setLengthCeilFlr(String lengthCeilFlr) {
        this.lengthCeilFlr = lengthCeilFlr;
    }

    public String getPocketDepth() {
        return pocketDepth;
    }

    public void setPocketDepth(String pocketDepth) {
        this.pocketDepth = pocketDepth;
    }

    public String getCarpetInst() {
        return carpetInst;
    }

    public void setCarpetInst(String carpetInst) {
        this.carpetInst = carpetInst;
    }

    public String getWindowTreatment() {
        return windowTreatment;
    }

    public void setWindowTreatment(String windowTreatment) {
        this.windowTreatment = windowTreatment;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
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

    public String getCeilingToFloor() {
        return ceilingToFloor;
    }

    public void setCeilingToFloor(String ceilingToFloor) {
        this.ceilingToFloor = ceilingToFloor;
    }

    public String getWallToWall() {
        return wallToWall;
    }

    public void setWallToWall(String wallToWall) {
        this.wallToWall = wallToWall;
    }

    public String getImageType() {
        return imageType;
    }

    public void setImageType(String imageType) {
        this.imageType = imageType;
    }

    public String getWindowApproval() {
        return windowApproval;
    }

    public void setWindowApproval(String windowApproval) {
        this.windowApproval = windowApproval;
    }

    public String getWindowStatus() {
        return windowStatus;
    }

    public void setWindowStatus(String windowStatus) {
        this.windowStatus = windowStatus;
    }

    public List<Allimage> getAllimages() {
        return allimages;
    }

    public void setAllimages(List<Allimage> allimages) {
        this.allimages = allimages;
    }

}
