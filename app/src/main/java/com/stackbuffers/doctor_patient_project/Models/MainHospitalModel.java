package com.stackbuffers.doctor_patient_project.Models;

public class MainHospitalModel {
    String id;
    String name,image;

    public MainHospitalModel(String id, String name, String image) {
        this.id = id;
        this.name = name;
        this.image = image;
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
}
