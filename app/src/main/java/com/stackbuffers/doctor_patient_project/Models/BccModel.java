package com.stackbuffers.doctor_patient_project.Models;

public class BccModel {
    String name,date,time,image,hospitalName;

    public BccModel(String name, String date, String time, String image, String hospitalName) {
        this.name = name;
        this.date = date;
        this.time = time;
        this.image = image;
        this.hospitalName = hospitalName;
    }

    public String getHospitalName() {
        return hospitalName;
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
}
