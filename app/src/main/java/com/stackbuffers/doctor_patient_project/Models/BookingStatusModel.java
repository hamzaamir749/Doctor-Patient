package com.stackbuffers.doctor_patient_project.Models;

public class BookingStatusModel {
    public String image,name,hospital,time,date,status;

    public BookingStatusModel(String image, String name, String hospital, String time, String date, String status) {
        this.image = image;
        this.name = name;
        this.hospital = hospital;
        this.time = time;
        this.date = date;
        this.status = status;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHospital() {
        return hospital;
    }

    public void setHospital(String hospital) {
        this.hospital = hospital;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
