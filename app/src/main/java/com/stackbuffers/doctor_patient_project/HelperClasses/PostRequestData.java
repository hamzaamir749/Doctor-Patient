package com.stackbuffers.doctor_patient_project.HelperClasses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.stackbuffers.doctor_patient_project.Models.MessageData;

public class PostRequestData {
    @SerializedName("data")
    @Expose
    private MessageData data;
    @SerializedName("to")
    @Expose
    private String to;

    public MessageData getData() {
        return data;
    }

    public void setData(MessageData data) {
        this.data = data;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

}
