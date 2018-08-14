
package com.valleyforge.cdi.generated.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UploadPhoto {

    @SerializedName("userID")
    @Expose
    private String userID;
    @SerializedName("imagepath")
    @Expose
    private String imagepath;
    @SerializedName("filename")
    @Expose
    private String filename;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("token")
    @Expose
    private String token;

    public String getUserID() {
        return userID;
    }

    public UploadPhoto(String type, String userID, String token, String imagepath, String filename)
    {
        this.type = type;
        this.userID = userID;
        this.token = token;
        this.imagepath = imagepath;
        this.filename =filename;

    }

}
