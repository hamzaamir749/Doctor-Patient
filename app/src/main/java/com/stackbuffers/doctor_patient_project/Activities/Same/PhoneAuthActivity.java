package com.stackbuffers.doctor_patient_project.Activities.Same;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.chaos.view.PinView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.stackbuffers.doctor_patient_project.Activities.Doctor.HomeScreenDoctorActivity;
import com.stackbuffers.doctor_patient_project.Activities.OTPActivity;
import com.stackbuffers.doctor_patient_project.Activities.Patient.HomeScreenPatientActivity;
import com.stackbuffers.doctor_patient_project.HelperClasses.GeneralData;
import com.stackbuffers.doctor_patient_project.HelperClasses.Session;
import com.stackbuffers.doctor_patient_project.HelperClasses.SharedHelper;
import com.stackbuffers.doctor_patient_project.HelperClasses.URLHelper;
import com.stackbuffers.doctor_patient_project.HelperClasses.UserSessionManager;
import com.stackbuffers.doctor_patient_project.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class PhoneAuthActivity extends AppCompatActivity {
    ImageView back;
    Button btn_verify_code;
    String mVerificationId;
    FirebaseAuth mAuth;
    ProgressDialog progressDialog;
    PinView pinView;
    SharedHelper sharedHelper;
    String phoneNumber, verificationcode;
    String activity = "", token = "";
    Context context;
    Session session;
    UserSessionManager userSessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_auth);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        activity = getIntent().getExtras().get("activityname").toString();
        //Bind your XML view here


        mAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        pinView = findViewById(R.id.firstPinView);
        btn_verify_code = findViewById(R.id.otp_verifycode);
        progressDialog = new ProgressDialog(this);
        sharedHelper = new SharedHelper();
        back=findViewById(R.id.phoneaut_back);
        context = this;
        userSessionManager = new UserSessionManager(context);
        //making Onclick event/Funtion for btn_send code to send code to user number
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            //   Toast.makeText(PhoneAuthActivity.this, "Exception", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Get new Instance ID token
                        token = task.getResult().getToken();


                        //    Toast.makeText(PhoneAuthActivity.this, token, Toast.LENGTH_SHORT).show();
                    }
                });
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

        btn_verify_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mVerificationId = sharedHelper.getKey(PhoneAuthActivity.this, "mVerificationId");
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, verificationcode);
                signInWithPhoneAuthCredential(credential);

            }
        });


    }

    //Paste outside the onCreate Method
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        progressDialog.setMessage("Please Wait");
        progressDialog.setTitle("Code Verifying");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            //Press ALT+Enter it automatically add classes to your class

                          /*  Intent intent = new Intent(getApplicationContext(), HomeScreenPatientActivity.class);
                            startActivity(intent);
                            finish();*/
                            if (activity.equals("login")) {
                                LoginUser();
                            } else {
                                RegisterUser();
                            }
                        } else {
                            // Sign in failed, display a message and update the UI

                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                            }
                        }
                    }
                });
    }

    private void LoginUser() {
        progressDialog.setTitle("Please Wait....");
        progressDialog.setMessage("Checking Your Account");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        StringRequest data = new StringRequest(Request.Method.POST, URLHelper.LOGIN, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                try {
                    //   Toast.makeText(context, response, Toast.LENGTH_SHORT).show();
                    JSONObject jsonObject = new JSONObject(response);
                    boolean status = jsonObject.getBoolean("status");
                    if (status) {
                        String id = jsonObject.getString("id");
                        String name = jsonObject.getString("name");
                        String phone = jsonObject.getString("phone");
                        String email = jsonObject.getString("email");
                        String image = jsonObject.getString("image");
                        String type;
                        if (sharedHelper.getKey(context, "logintype").equals("user")) {
                            type = "user";
                        } else {
                            type = "doctor";
                        }
                        session = new Session(id, email, name, phone, URLHelper.BASE_IMAGE + image, type);
                        userSessionManager.setSessionDetails(session);
                        userSessionManager.setLoggedIn(true);
                        if (sharedHelper.getKey(context, "logintype").equals("user")) {
                            SendUserToHomeActivity();
                        } else {
                            SendDoctorToHomeActivity();
                        }

                    } else {
                            Toast.makeText(PhoneAuthActivity.this, "Invalid Phone Or Password", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                String json = null;
                String Message;
                NetworkResponse response = error.networkResponse;
                if (response != null && response.data != null) {
                    try {
                        JSONObject errorObj = new JSONObject(new String(response.data));
                        if (response.getClass().equals(TimeoutError.class)) {
                            LoginUser();
                            return;
                        }
                        if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
                            try {
                                displayMessage(errorObj.optString("message"));
                            } catch (Exception e) {
                                displayMessage("Some Thing went wrong");
                            }
                        } else if (response.statusCode == 401) {

                        } else if (response.statusCode == 422) {

                            json = GeneralData.trimMessage(new String(response.data));
                            if (json != "" && json != null) {
                                displayMessage(json);
                            } else {
                                displayMessage("Please try again");
                            }

                        } else if (response.statusCode == 503) {
                            displayMessage("Server Down");
                        } else {
                            displayMessage("Please try again");
                        }

                    } catch (Exception e) {
                        displayMessage("Some thing went wrong");
                    }

                } else {
                    if (error instanceof NoConnectionError) {
                        displayMessage("Connect Internet");
                    } else if (error instanceof NetworkError) {
                        displayMessage("Connect Internet");
                    } else if (error instanceof TimeoutError) {
                        LoginUser();
                    }
                }
            }
        }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("phone", sharedHelper.getKey(context, "loginphone"));
                map.put("password", sharedHelper.getKey(context, "loginpassword"));
                map.put("type", sharedHelper.getKey(context, "logintype"));
                map.put("token", token);
                return map;
            }
        };
        Volley.newRequestQueue(PhoneAuthActivity.this).add(data);

    }

    private void SendDoctorToHomeActivity() {
        Intent intent = new Intent(getApplicationContext(), HomeScreenDoctorActivity.class);
        startActivity(intent);
        finishAffinity();
    }

    private void RegisterUser() {
        progressDialog.setTitle("Please Wait....");
        progressDialog.setMessage("Creating your Account");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        StringRequest data = new StringRequest(Request.Method.POST, URLHelper.SIGNUP, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                try {

                    JSONObject jsonObject = new JSONObject(response);
                    boolean status = jsonObject.getBoolean("status");
                    if (status) {
                        String id = jsonObject.getString("id");
                        String name = jsonObject.getString("name");
                        String phone = jsonObject.getString("phone");
                        String email = jsonObject.getString("email");
                        String image = jsonObject.getString("image");
                        session = new Session(id, email, name, phone, URLHelper.BASE_IMAGE + image, "user");
                        userSessionManager.setSessionDetails(session);
                        userSessionManager.setLoggedIn(true);
                        SendUserToHomeActivity();
                    } else {
                        Toast.makeText(context, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                        //   Toast.makeText(PhoneAuthActivity.this, "Account not created", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
                String json = null;
                String Message;
                NetworkResponse response = error.networkResponse;
                if (response != null && response.data != null) {
                    try {
                        JSONObject errorObj = new JSONObject(new String(response.data));
                        if (response.getClass().equals(TimeoutError.class)) {
                            RegisterUser();
                            return;
                        }
                        if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
                            try {
                                displayMessage(errorObj.optString("message"));
                            } catch (Exception e) {
                                displayMessage("Some Thing went wrong");
                            }
                        } else if (response.statusCode == 401) {

                        } else if (response.statusCode == 422) {

                            json = GeneralData.trimMessage(new String(response.data));
                            if (json != "" && json != null) {
                                displayMessage(json);
                            } else {
                                displayMessage("Please try again");
                            }

                        } else if (response.statusCode == 503) {
                            displayMessage("Server Down");
                        } else {
                            displayMessage("Please try again");
                        }

                    } catch (Exception e) {
                        displayMessage("Some thing went wrong");
                    }

                } else {
                    if (error instanceof NoConnectionError) {
                        displayMessage("Connect Internet");
                    } else if (error instanceof NetworkError) {
                        displayMessage("Connect Internet");
                    } else if (error instanceof TimeoutError) {
                        RegisterUser();
                    }
                }
            }
        }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("name", sharedHelper.getKey(context, "signUp_name"));
                map.put("email", sharedHelper.getKey(context, "signUp_email"));
                map.put("phone", sharedHelper.getKey(context, "signUp_phone"));
                map.put("password", sharedHelper.getKey(context, "signUp_password"));
                map.put("gender", sharedHelper.getKey(context, "signUp_gender"));
                map.put("city", sharedHelper.getKey(context, "signUp_city"));
                map.put("image", sharedHelper.getKey(context, "signUp_image"));
                map.put("type",sharedHelper.getKey(context,"signUp_type"));
                map.put("token", token);
                return map;
            }
        };
        data.setRetryPolicy(new DefaultRetryPolicy(
                120000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(PhoneAuthActivity.this).add(data);
    }

    public void displayMessage(String toastString) {
        try {
            Snackbar.make(getCurrentFocus(), toastString, Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show();
        } catch (Exception e) {
            try {
                Toast.makeText(context, "" + toastString, Toast.LENGTH_SHORT).show();
            } catch (Exception ee) {
                ee.printStackTrace();
            }
        }
    }

    private void SendUserToHomeActivity() {
        Intent intent = new Intent(getApplicationContext(), HomeScreenPatientActivity.class);
        startActivity(intent);
        finishAffinity();
    }
}
