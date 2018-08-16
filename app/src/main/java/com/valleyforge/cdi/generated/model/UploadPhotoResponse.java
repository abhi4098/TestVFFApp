
package com.valleyforge.cdi.generated.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UploadPhotoResponse {

    @SerializedName("type")
    @Expose
    private Integer type;
    @SerializedName("tokenid")
    @Expose
    private String tokenid;
    @SerializedName("imageurl")
    @Expose
    private String imageurl;
    @SerializedName("msg")
    @Expose
    private String msg;

    @SerializedName("imageid")
    @Expose
    private String imageId;

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public String getTokenid() {
        return tokenid;
    }

    public void setTokenid(String tokenid) {
        this.tokenid = tokenid;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
