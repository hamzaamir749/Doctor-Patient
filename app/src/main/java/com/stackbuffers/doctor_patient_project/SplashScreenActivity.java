package com.stackbuffers.doctor_patient_project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;

import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.Circle;
import com.stackbuffers.doctor_patient_project.Activities.Doctor.HomeScreenDoctorActivity;
import com.stackbuffers.doctor_patient_project.Activities.LoginActivity;
import com.stackbuffers.doctor_patient_project.Activities.Patient.HomeScreenPatientActivity;
import com.stackbuffers.doctor_patient_project.HelperClasses.Session;
import com.stackbuffers.doctor_patient_project.HelperClasses.UserSessionManager;

public class SplashScreenActivity extends AppCompatActivity {
    ProgressBar progressBar;
    UserSessionManager userSessionManager;
    Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        progressBar = findViewById(R.id.spin_kit);
        userSessionManager=new UserSessionManager(this);
        session=userSessionManager.getSessionDetails();

        Sprite circle = new Circle();
        progressBar.setIndeterminateDrawable(circle);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
               if (userSessionManager.isLoggedIn())
               {
                  if (session.getType().equals("user")){
                      Intent intent = new Intent(getApplicationContext(), HomeScreenPatientActivity.class);
                      startActivity(intent);
                      finish();
                  }
                  else {
                      Intent intent = new Intent(getApplicationContext(), HomeScreenDoctorActivity.class);
                      startActivity(intent);
                      finish();
                  }
               }
               else {   Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                   startActivity(intent);
                   finish();

               }
            }
        }, 4000);
    }
}
