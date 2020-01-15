package com.bigipis.bigipis.source;

import com.google.type.LatLng;

import java.util.Date;
import java.util.List;

public class Route {
    private User user;
    private Date createDate;
    private List<LatLng> markersList;
    private String description;
    private int rating;
    private String[] chipsNames;

    public Route(User user, Date createDate, List<LatLng> markersList, String description, int rating, String[] chipsNames, boolean isOpenMap) {
        this.user = user;
        this.createDate = createDate;
        this.markersList = markersList;
        this.description = description;
        this.rating = rating;
        this.chipsNames = chipsNames;
    }

    Route(){}

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public List<LatLng> getMarkersList() {
        return markersList;
    }

    public void setMarkersList(List<LatLng> markersList) {
        this.markersList = markersList;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String[] getChipsNames() {
        return chipsNames;
    }

    public void setChipsNames(String[] chipsNames) {
        this.chipsNames = chipsNames;
    }
}
