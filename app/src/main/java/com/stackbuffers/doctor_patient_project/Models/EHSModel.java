package com.stackbuffers.doctor_patient_project.Models;

public class EHSModel {
    String name,image,channel,token;

    public EHSModel(String name, String image, String channel, String token) {
        this.name = name;
        this.image = image;
        this.channel = channel;
        this.token = token;
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
