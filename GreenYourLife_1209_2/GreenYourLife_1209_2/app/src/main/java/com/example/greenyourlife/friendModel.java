package com.example.greenyourlife;

public class friendModel {

    String userName;
    String uid;
    String image;
    Integer carbonPoints;
    Integer steps;

    public friendModel() {

    }

    public friendModel(String userName, String uid, String image, Integer carbonPoints, Integer steps) {
        this.carbonPoints = carbonPoints;
        this.steps = steps;
        this.userName = userName;
        this.uid = uid;
        this.image = image;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Integer getCarbonPoints() {
        return carbonPoints;
    }

    public void setCarbonPoints(Integer carbonPoints) {
        this.carbonPoints = carbonPoints;
    }

    public Integer getSteps() {
        return steps;
    }

    public void setSteps(Integer steps) {
        this.steps = steps;
    }
}
