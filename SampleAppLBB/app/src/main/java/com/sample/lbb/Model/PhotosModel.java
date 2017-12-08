package com.sample.lbb.Model;

import android.util.Log;

/**
 * Created by harshitgupta on 08/12/17.
 */

public class PhotosModel {

    String bucketId, imageBucket, imagePath, imageName;

    public PhotosModel(String bucketId, String imageBucket, String imagePath, String imageName) {
        this.bucketId = bucketId;
        this.imageBucket = imageBucket;
        this.imageName = imageName;
        this.imagePath = imagePath;
    }

    public String getBucketId() {
        return bucketId;
    }

    public String getImageBucket() {
        return imageBucket;
    }

    public String getImageName() {
        return imageName;
    }

    public String getImagePath() {
        return imagePath;
    }


}
