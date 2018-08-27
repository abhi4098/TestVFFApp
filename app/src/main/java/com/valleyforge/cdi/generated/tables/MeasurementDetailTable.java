package com.valleyforge.cdi.generated.tables;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
@Table(name = "MeasurementDetailTable")
public class MeasurementDetailTable extends Model{

   /* @Column(name="m_id")
    public String m_id;*/


    @Column(name="floorPlanId")
    public String floorPlanId;

    @Column(name="roomId")
    public String roomId;

    @Column(name="windowName")
    public String windowName;

    @Column(name="wallWidth")
    public String wallWidth;

    @Column(name="widthLeftWindow")
    public String widthLeftWindow;

    @Column(name="ibWidthWindow")
    public String ibWidthWindow;

    @Column(name="ibLengthWindow")
    public String ibLengthWindow;

    @Column(name="widthRightWindow")
    public String widthRightWindow;

    @Column(name="lengthCeilFlr")
    public String lengthCeilFlr;

    @Column(name="pocketDepth")
    public String pocketDepth;

    @Column(name="carpetInst")
    public String carpetInst;

    @Column(name="windowCompletionStatus")
    public String windowCompletionStatus;

    @Column(name="windowApprovalCheck")
    public String windowApprovalCheck;


    @Column(name="userId")
    public String userId;

    @Column(name="windowId")
    public String windowId;

    public MeasurementDetailTable() {
        super();
    }

    public MeasurementDetailTable( /*String m_id*/String floorPlanId,String roomId,String windowName,String wallWidth,
                                  String widthLeftWindow,String ibWidthWindow,String ibLengthWindow,String widthRightWindow,
                                  String lengthCeilFlr,String pocketDepth,String carpetInst,String windowCompletionStatus,
                                  String windowApprovalCheck,String userId,String windowId) {
        super();
       /* this.m_id = m_id;*/
        this.floorPlanId = floorPlanId;
        this.roomId = roomId;
        this.windowName =windowName;

        this.wallWidth = wallWidth;
        this.widthLeftWindow = widthLeftWindow;
        this.ibWidthWindow = ibWidthWindow;
        this.ibLengthWindow =ibLengthWindow;

        this.widthRightWindow = widthRightWindow;
        this.lengthCeilFlr = lengthCeilFlr;
        this.pocketDepth = pocketDepth;
        this.carpetInst =carpetInst;

        this.windowCompletionStatus = windowCompletionStatus;
        this.windowApprovalCheck = windowApprovalCheck;
        this.userId = userId;
        this.windowId =windowId;
    }


}

