package com.stackbuffers.doctor_patient_project.HelperClasses;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedHelper {
    SharedPreferences storeinfo;
    public SharedPreferences.Editor editor;
    public Context context;
    int private_mode = 0;

    private static final String storeDBName = "storeinfo"; //userData
    public void putKey(Context context,String key,String value) {
        this.context = context;
        storeinfo = context.getSharedPreferences(storeDBName, private_mode);
        SharedPreferences.Editor DataDetails=storeinfo.edit();
        DataDetails.putString(key,value);
        DataDetails.apply();
    }

    public String getKey(Context context,String key){
        this.context = context;
        storeinfo = context.getSharedPreferences(storeDBName, private_mode);
        String keyData=storeinfo.getString(key ,"empty");
        return keyData;
    }

    public void clearStoreInfoData() {
        SharedPreferences.Editor clientSpEditor = storeinfo.edit();
        clientSpEditor.clear();
        clientSpEditor.apply();
    }
}
