package com.stackbuffers.doctor_patient_project.Models;

public class MessagesModel {
    String id,name,image,token,type;

    public MessagesModel(String id, String name, String image, String token, String type) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.token = token;
        this.type = type;
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

    public String getToken() {
        return token;
    }

    public String getType() {
        return type;
    }
}
