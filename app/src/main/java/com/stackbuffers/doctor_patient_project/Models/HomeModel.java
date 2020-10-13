package com.stackbuffers.doctor_patient_project.Models;

public class HomeModel {
    int icons;
    String name,total;

    public HomeModel(int icons, String name, String total) {
        this.icons = icons;
        this.name = name;
        this.total = total;
    }

    public int getIcons() {
        return icons;
    }

    public void setIcons(int icons) {
        this.icons = icons;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }
}
