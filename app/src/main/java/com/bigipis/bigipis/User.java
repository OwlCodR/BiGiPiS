package com.bigipis.bigipis;

public class User {
    private int weight;         // кг
    private int height;         // см
    private String nickname;
    private int iconId;
    private double allDistance;    // метр
    private double maxDistance;    // метр
    private double maxSpeed;       // м/c
    private int likes;
    private int dislikes;
    private int rating;
    //private int battery;           // %
    private String serial_S;       // S|1|000001
    private String serial_W;       // W|1|000001
    private String uid;

    public User(int weight, int height, String nickname, int iconId, double allDistance,
                double maxDistance, double maxSpeed, int likes, int dislikes, int rating,
                String serial_S, String serial_W, String uid) {
        this.weight = weight;
        this.height = height;
        this.nickname = nickname;
        this.iconId = iconId;
        this.allDistance = allDistance;
        this.maxDistance = maxDistance;
        this.maxSpeed = maxSpeed;
        this.likes = likes;
        this.dislikes = dislikes;
        this.rating = rating;
        this.serial_S = serial_S;
        this.serial_W = serial_W;
        this.uid = uid;
    }

    User(){
        this.weight = 0;
        this.height = 0;
        this.nickname = "";
        this.allDistance = 0;
        this.maxDistance = 0;
        this.maxSpeed = 0;
        this.likes = 0;
        this.dislikes = 0;
        this.rating = 0;
        this.iconId = 0;
        this.serial_S = "";
        this.serial_W = "";
        this.uid = null;
        //this.battery = 0;
    }

    /*
    public void setHashMapUser() {
        hashMapUser.clear();

        hashMapUser.put("weight", weight);
        hashMapUser.put("height", height);
        hashMapUser.put("nickname", nickname);
        hashMapUser.put("iconId", iconId);
        hashMapUser.put("allDistance", allDistance);
        hashMapUser.put("maxDistance", maxDistance);
        hashMapUser.put("maxSpeed", maxSpeed);
        hashMapUser.put("allLikes", allLikes);
        hashMapUser.put("allDislikes", allDislikes);
        hashMapUser.put("rating", rating);
        hashMapUser.put("serial_S", serial_S);
        hashMapUser.put("serial_W", serial_W);
    }

    public HashMap<String, Object> getHashMapUser() {
        return hashMapUser;
    }
    */

    public void setDislikes(int dislikes) {
        this.dislikes = dislikes;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getIconId() {
        return iconId;
    }

    public void setIconId(int iconId) {
        this.iconId = iconId;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public double getAllDistance() {
        return allDistance;
    }

    public void setAllDistance(double allDistance) {
        this.allDistance = allDistance;
    }

    public double getMaxDistance() {
        return maxDistance;
    }

    public void setMaxDistance(double maxDistance) {
        this.maxDistance = maxDistance;
    }

    public double getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(double maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int allLikes) {
        this.likes = allLikes;
    }

    public int getDislikes() {
        return dislikes;
    }

    public void setAllDislikes(int allDislikes) {
        this.dislikes = allDislikes;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getSerial_S() {
        return serial_S;
    }

    public void setSerial_S(String serial_S) {
        this.serial_S = serial_S;
    }

    public String getSerial_W() {
        return serial_W;
    }

    public void setSerial_W(String serial_W) {
        this.serial_W = serial_W;
    }

    /*
    public int getBattery() {
        return battery;
    }

    public void setBattery(int battery) {
        this.battery = battery;
    }
    */
}
