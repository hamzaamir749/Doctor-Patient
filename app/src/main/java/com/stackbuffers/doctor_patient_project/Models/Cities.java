package com.stackbuffers.doctor_patient_project.Models;

public class Cities {
    public int id;
    public String name;

    public Cities(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
