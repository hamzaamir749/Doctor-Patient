package com.stackbuffers.doctor_patient_project.Activities.Patient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
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
import com.squareup.picasso.Picasso;
import com.stackbuffers.doctor_patient_project.Adapters.SearchDoctorAdapter;
import com.stackbuffers.doctor_patient_project.HelperClasses.GeneralData;
import com.stackbuffers.doctor_patient_project.HelperClasses.SharedHelper;
import com.stackbuffers.doctor_patient_project.HelperClasses.URLHelper;
import com.stackbuffers.doctor_patient_project.Models.SearchDoctorConsultantModel;
import com.stackbuffers.doctor_patient_project.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class HospitalDetailsActivity extends AppCompatActivity {
    Context context;
    ProgressBar progressBar;
    CircleImageView img_imageView;
    SharedHelper sharedHelper;
    TextView txt_name, txt_city, txt_phone;
    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    List<SearchDoctorConsultantModel> list;
    ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital_details);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        initView();
        getDetailsOfHospital();
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void initView() {
        txt_name = findViewById(R.id.hospital_details_name);
        txt_city = findViewById(R.id.hospital_details_city);
        txt_phone = findViewById(R.id.hospital_details_phone);
        recyclerView = findViewById(R.id.hospital_details_Recycler);
        progressBar = findViewById(R.id.hospital_details_spin_kit);
        sharedHelper = new SharedHelper();
        back=findViewById(R.id.hospital_details_back);
        img_imageView = findViewById(R.id.hospital_details_profileimage);
        context = this;
        Sprite circle = new Circle();
        progressBar.setIndeterminateDrawable(circle);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
    }


    private void getDetailsOfHospital() {

        list = new ArrayList<>();
        progressBar.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLHelper.GET_HOSPITALS_PROFILE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
              //  Toast.makeText(context, response, Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                try {
                    JSONObject doctor;
                    JSONObject jsonObject = new JSONObject(response);
                    boolean status = jsonObject.getBoolean("status");
                    if (status) {
                        String name = jsonObject.getString("hospital_name");
                        String city = jsonObject.getString("city");
                        String phone = jsonObject.getString("phone");
                        String image = jsonObject.getString("hospital_image");
                        txt_name.setText(name);
                        txt_city.setText(city);
                        txt_phone.setText(phone);


                        Picasso.get().load(URLHelper.BASE_IMAGE + image).into(img_imageView);

                        JSONArray jsonArray = jsonObject.getJSONArray("doctors");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            doctor = jsonArray.getJSONObject(i);
                            String id = doctor.getString("d_id");
                            String namee = doctor.getString("doctor_name");
                            String imagee = doctor.getString("doctor_image");
                            String Speciality = doctor.getString("speciality");
                            SearchDoctorConsultantModel model = new SearchDoctorConsultantModel("DR. " + namee, Speciality, URLHelper.BASE_IMAGE + imagee, id,"",doctor.getString("token"));
                            list.add(model);
                        }
                        SearchDoctorAdapter adapter = new SearchDoctorAdapter(getApplicationContext(), list);
                        recyclerView.setAdapter(adapter);

                    } else {
                    //    Toast.makeText(context, "No Details", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {

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
                            getDetailsOfHospital();
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
                        getDetailsOfHospital();
                    }
                }
            }

        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("id", sharedHelper.getKey(context, "hid"));
                return map;
            }
        };
        Volley.newRequestQueue(context).add(stringRequest);
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
