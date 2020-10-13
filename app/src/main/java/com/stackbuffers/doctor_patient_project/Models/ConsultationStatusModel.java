package com.stackbuffers.doctor_patient_project.Models;

public class ConsultationStatusModel {
    public String image,name,hospital,time,date,status;
    public String id;
    public String token,channel;

    public ConsultationStatusModel(String image, String name, String hospital, String time, String date, String status, String id, String token, String channel) {
        this.image = image;
        this.name = name;
        this.hospital = hospital;
        this.time = time;
        this.date = date;
        this.status = status;
        this.id = id;
        this.token = token;
        this.channel = channel;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getImage() {
        return image;
    }

    public String getName() {
        return name;
    }

    public String getHospital() {
        return hospital;
    }

    public String getTime() {
        return time;
    }

    public String getDate() {
        return date;
    }

    public String getStatus() {
        return status;
    }

    public String getId() {
        return id;
    }
}
