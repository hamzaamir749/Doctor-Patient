package com.stackbuffers.doctor_patient_project.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.chaos.view.PinView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.stackbuffers.doctor_patient_project.Activities.Patient.HomeScreenPatientActivity;
import com.stackbuffers.doctor_patient_project.HelperClasses.SharedHelper;
import com.stackbuffers.doctor_patient_project.R;

import java.util.concurrent.TimeUnit;


public class OTPActivity extends AppCompatActivity {
    Button btn_otp_verifycode;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private FirebaseAuth mAuth;
    private String mVerificationId, verificationcode;
    private PhoneAuthProvider phoneAuthProvider;
    ProgressDialog progressDialog;
    SharedHelper sharedHelper;
    PinView pinView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        btn_otp_verifycode = findViewById(R.id.otp_verifycode);
        pinView = findViewById(R.id.firstPinView);
        progressDialog = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        phoneAuthProvider = PhoneAuthProvider.getInstance();
        sharedHelper = new SharedHelper();
        String phoneNo = sharedHelper.getKey(OTPActivity.this, "phoneno");


        pinView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                verificationcode = s.toString();
            }
        });
        btn_otp_verifycode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(OTPActivity.this, verificationcode, Toast.LENGTH_LONG).show();
            }
        });


    }


    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        progressDialog.setTitle("Code Verification");
        progressDialog.setMessage("Please Wait, while we are verifying your Code");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                progressDialog.dismiss();
                if (task.isSuccessful()) {
                    Intent intent = new Intent(getApplicationContext(), HomeScreenPatientActivity.class);
                    startActivity(intent);
                } else {

                    progressDialog.dismiss();
                    Toast.makeText(OTPActivity.this, "Enter Correct Code", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
