package com.stackbuffers.doctor_patient_project.Models;

public class HospitalModel {

    public String name;
    public int id;

    public HospitalModel(String name,  int id) {
        this.name = name;

        this.id = id;
    }



    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }
}
