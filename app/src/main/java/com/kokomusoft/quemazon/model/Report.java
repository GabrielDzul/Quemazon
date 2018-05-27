package com.kokomusoft.quemazon.model;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by ASUS on 14/05/2018.
 */

public class Report {
    private String id;
    private String userId;
    private String date;
    private String intensity;
    private LatLng latLng;
    private String status;
    private String imageUrl;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getIntensity() {
        return intensity;
    }

    public void setIntensity(String intensity) {
        this.intensity = intensity;
    }

    @Override
    public String toString() {
        return "Report{" +
                "intensity='" + intensity + '\'' +
                ", latLng=" + latLng +
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }
}
