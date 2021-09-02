package com.bigipis.bigipis.source;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class Route {
    private String userID;
    private long date;
    private List<Map<String, Double>> pointsList;
    private int rating;
    private boolean isPublic;
    private List<String> chipsNames;

    private String userName;

    public Route(String userID, String userName, long date, List<Map<String, Double>> pointsList, int rating, List<String> tags, boolean isPublic) {
        this.userID = userID;
        this.date = date;
        this.pointsList = pointsList;
        this.rating = rating;
        this.chipsNames = tags;
        this.isPublic = isPublic;
        this.userName = userName;
    }

    Route(){}

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }
    public long getDate() {
        return date;
    }


    public List<Map<String, Double>> getPointsList() {
        return pointsList;
    }

    public void setPointsList(List<Map<String, Double>> pointsList) {
        this.pointsList = pointsList;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public List<String> getChipsNames() {
        return chipsNames;
    }

    public void setChipsNames(List<String> chipsNames) {
        this.chipsNames = chipsNames;
    }
}
