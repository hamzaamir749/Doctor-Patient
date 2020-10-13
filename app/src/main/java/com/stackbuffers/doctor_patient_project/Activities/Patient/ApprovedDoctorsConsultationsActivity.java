package com.stackbuffers.doctor_patient_project.Activities.Patient;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.stackbuffers.doctor_patient_project.HelperClasses.SharedHelper;
import com.stackbuffers.doctor_patient_project.R;

public class ApprovedDoctorsConsultationsActivity extends AppCompatActivity {
    TextView txt;
    SharedHelper sharedHelper;
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_approved_doctors_consultations_actvity);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        context=this;
        sharedHelper=new SharedHelper();
        txt=findViewById(R.id.adca_txt);
        txt.setText(sharedHelper.getKey(context,"activitytxt"));
    }
}
