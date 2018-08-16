package com.valleyforge.cdi.generated.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class ImageList {
    @SerializedName("image_url")
    @Expose
    private String  imageUrl;
    @SerializedName("image_type")
    @Expose
    private String imageType;

    @SerializedName("image_id")
    @Expose
    private String imageId;
   
    public String getimageType() {
        return imageType;
    }

    public void setimageType(String imageType) {
        this.imageType = imageType;
    }

    public String getimageUrl() {
        return imageUrl;
    }

    public void setimageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }


    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }
}
