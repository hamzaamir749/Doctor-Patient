package com.stackbuffers.doctor_patient_project.Models;

public class EPModel {
    String id,name,image,channel,token;

    public EPModel(String id, String name, String image, String channel, String token) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.channel = channel;
        this.token = token;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    public String getChannel() {
        return channel;
    }

    public String getToken() {
        return token;
    }
}
