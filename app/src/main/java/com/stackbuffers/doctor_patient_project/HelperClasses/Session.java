package com.stackbuffers.doctor_patient_project.HelperClasses;

public class Session {
public String id;
public String email,name,phone,image,type;

    public Session(String id, String email, String name, String phone, String image, String type) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.phone = phone;
        this.image = image;
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getImage() {
        return image;
    }
}
