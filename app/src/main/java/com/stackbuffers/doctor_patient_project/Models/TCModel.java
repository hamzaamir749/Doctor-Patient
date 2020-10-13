package com.stackbuffers.doctor_patient_project.Models;

public class TCModel {
    String name,date,time,image,id,appId;
    String token="",channel="";

    public TCModel(String name, String date, String time, String image, String id, String appId, String token, String channel) {
        this.name = name;
        this.date = date;
        this.time = time;
        this.image = image;
        this.id = id;
        this.appId = appId;
        this.token = token;
        this.channel = channel;
    }

    public String getToken() {
        return token;
    }

    public String getChannel() {
        return channel;
    }

    public String getAppId() {
        return appId;
    }

    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getImage() {
        return image;
    }

    public String getId() {
        return id;
    }
}
