package com.stackbuffers.doctor_patient_project.Models;

public class TBModel {
    String name,date,time,image,id,hospitalName;

    public TBModel(String name, String date, String time, String image, String id, String hospitalName) {
        this.name = name;
        this.date = date;
        this.time = time;
        this.image = image;
        this.id = id;
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

    public String getId() {
        return id;
    }
}
