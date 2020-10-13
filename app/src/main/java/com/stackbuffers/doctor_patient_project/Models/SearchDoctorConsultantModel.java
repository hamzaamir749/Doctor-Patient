package com.stackbuffers.doctor_patient_project.Models;

public class SearchDoctorConsultantModel {
    String name, specialization;
    String Image;
    String id,type,token;

    public SearchDoctorConsultantModel(String name, String specialization, String image, String id, String type, String token) {
        this.name = name;
        this.specialization = specialization;
        Image = image;
        this.id = id;
        this.type = type;
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public String getType() {
        return type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }
}
