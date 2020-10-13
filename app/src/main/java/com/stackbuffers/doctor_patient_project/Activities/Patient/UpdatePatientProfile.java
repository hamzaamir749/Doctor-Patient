package com.stackbuffers.doctor_patient_project.Activities.Patient;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

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
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.Circle;
import com.google.android.material.snackbar.Snackbar;
import com.rilixtech.widget.countrycodepicker.CountryCodePicker;
import com.squareup.picasso.Picasso;
import com.stackbuffers.doctor_patient_project.HelperClasses.AppHelper;
import com.stackbuffers.doctor_patient_project.HelperClasses.GeneralData;
import com.stackbuffers.doctor_patient_project.HelperClasses.Session;
import com.stackbuffers.doctor_patient_project.HelperClasses.SharedHelper;
import com.stackbuffers.doctor_patient_project.HelperClasses.URLHelper;
import com.stackbuffers.doctor_patient_project.HelperClasses.UserSessionManager;
import com.stackbuffers.doctor_patient_project.HelperClasses.VolleyMultipartRequest;
import com.stackbuffers.doctor_patient_project.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class UpdatePatientProfile extends AppCompatActivity {
    ProgressBar progressBar;
    CircleImageView img_imageView;
    EditText edt_name, edt_email, edt_phone, edt_password;
    CountryCodePicker cpp;
    public static int SELECT_PHOTO = 1000;
    Context context;
    SharedHelper sharedHelper;
    ProgressDialog progressDialog;
    Button btn_update;
    UserSessionManager userSessionManager;
    Session session;
    CountryCodePicker codePicker;
    String userID="",userType="";
    ImageView back;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_patient_profile);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        userType=getIntent().getStringExtra("typeuser");
        initViews();
        SetInfo();
        img_imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PHOTO);

            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatePatient();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SELECT_PHOTO && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                img_imageView.setImageBitmap(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void SetInfo() {
        progressBar.setVisibility(View.VISIBLE);
        StringRequest request = new StringRequest(Request.Method.POST, URLHelper.GET_USER_PROFILE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressBar.setVisibility(View.GONE);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean status = jsonObject.getBoolean("status");
                    if (status) {
                        String name = jsonObject.getString("name");
                        String email = jsonObject.getString("email");
                        String phone = jsonObject.getString("phone");
                        String image = jsonObject.getString("image");
                        userID=jsonObject.getString("user_id");
                        edt_email.setText(email);
                        edt_name.setText(name);

                        edt_phone.setText(phone);
                        Picasso.get().load(URLHelper.BASE_IMAGE+image).into(img_imageView);
                    }
                } catch (JSONException e) {
                  //  Toast.makeText(context, "JSON Exception: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                String json = null;
                String Message;
                NetworkResponse response = error.networkResponse;
                if (response != null && response.data != null) {
                    try {
                        JSONObject errorObj = new JSONObject(new String(response.data));
                        if (response.getClass().equals(TimeoutError.class)) {
                            SetInfo();
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
                        SetInfo();
                    }
                }
            }

        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("id", String.valueOf(session.getId()));
                map.put("type",userType);
                return map;
            }
        };
        Volley.newRequestQueue(context).add(request);
    }

    private void updatePatient() {
        if (edt_name.getText().toString().isEmpty() && edt_email.getText().toString().isEmpty() && edt_phone.getText().toString().isEmpty()) {
            edt_name.setError("Please Enter Here");
            edt_email.setError("Please Enter Here");
            edt_phone.setError("Please Enter Here");
            edt_password.setError("Please Enter Here");
        } else if (edt_name.getText().toString().isEmpty()) {
            edt_name.setError("Please Enter Here");
        } else if (edt_email.getText().toString().isEmpty()) {
            edt_email.setError("Please Enter Here");
        } else if (edt_phone.getText().toString().isEmpty()) {
            edt_phone.setError("Please Enter Here");
        } else {


            progressDialog.setTitle("Phone Verification");
            progressDialog.setMessage("Please Wait");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            VolleyMultipartRequest request = new VolleyMultipartRequest(Request.Method.POST, URLHelper.UPDATE_USER_PROFILE, new Response.Listener<NetworkResponse>() {
                @Override
                public void onResponse(NetworkResponse response) {
                    progressDialog.dismiss();
                    String res = new String(response.data);
                    try {
                        JSONObject jsonObject = new JSONObject(res);
                        boolean status = jsonObject.getBoolean("status");
                        if (status) {

                            String name = jsonObject.getString("name");
                            String email = jsonObject.getString("email");
                            String phone = jsonObject.getString("phone");
                            String image = jsonObject.getString("image");
                            String id = jsonObject.getString("id");
                            String sessionType=session.getType();
                            session=new Session(id,email,name,phone,URLHelper.BASE_IMAGE+image,sessionType);
                            userSessionManager.setSessionDetails(session);
                            Intent intent=new Intent(getApplicationContext(),HomeScreenPatientActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                        //    Toast.makeText(context, "Not Submitted", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                    //    Toast.makeText(context, "Exception: " + e.getMessage(), Toast.LENGTH_LONG).show();
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
                                updatePatient();
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
                            updatePatient();
                        }
                    }
                }


            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> map = new HashMap<>();
                    map.put("id", String.valueOf(session.getId()));
                    map.put("name", edt_name.getText().toString());
                    map.put("email", edt_email.getText().toString());
                    map.put("password", edt_password.getText().toString());
                    map.put("phone",edt_phone.getText().toString());
                    return map;
                }

                @Override
                protected Map<String, DataPart> getByteData() throws AuthFailureError {
                    Map<String, VolleyMultipartRequest.DataPart> dataPartMap = new HashMap<>();
                    dataPartMap.put("image", new VolleyMultipartRequest.DataPart("profile_image", AppHelper.getFileDataFromDrawable(img_imageView.getDrawable()), "image/jpeg"));
                    return dataPartMap;
                }
            };
            request.setRetryPolicy(new DefaultRetryPolicy(
                    160000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            Volley.newRequestQueue(context).add(request);


        }

    }

    private void initViews() {
        back=findViewById(R.id.update_back);
        codePicker=findViewById(R.id.update_cpp);
        btn_update = findViewById(R.id.update_btnsignup);
        progressBar = findViewById(R.id.update_spin_kit);
        edt_name = findViewById(R.id.update_username);
        edt_email = findViewById(R.id.update_email);
        edt_password = findViewById(R.id.update_password);
        edt_phone = findViewById(R.id.update_phone);
        cpp = findViewById(R.id.update_cpp);
        img_imageView = findViewById(R.id.update_profileimage);
        progressDialog = new ProgressDialog(this);
        sharedHelper = new SharedHelper();
        context = this;
        Sprite circle = new Circle();
        progressBar.setIndeterminateDrawable(circle);
        userSessionManager = new UserSessionManager(context);
        session = userSessionManager.getSessionDetails();
    }

    private void displayMessage(String toastString) {
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
}
