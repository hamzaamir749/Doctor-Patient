package com.stackbuffers.doctor_patient_project.Activities.Patient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
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
import com.stackbuffers.doctor_patient_project.Adapters.HomeHospitalsAdapter;
import com.stackbuffers.doctor_patient_project.Adapters.SearchDoctorAdapter;
import com.stackbuffers.doctor_patient_project.HelperClasses.GeneralData;
import com.stackbuffers.doctor_patient_project.HelperClasses.URLHelper;
import com.stackbuffers.doctor_patient_project.Models.HomeModel;
import com.stackbuffers.doctor_patient_project.Models.HospitalModel;
import com.stackbuffers.doctor_patient_project.Models.MainHospitalModel;
import com.stackbuffers.doctor_patient_project.Models.SearchDoctorConsultantModel;
import com.stackbuffers.doctor_patient_project.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HospitalsActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    List<MainHospitalModel> list=null;
    Context context;
    ProgressBar progressBar;
    ImageView back;
    TextView txtNotAvailable;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospitals);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        initViews();
        setRecycler();
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }


    private void initViews() {
        txtNotAvailable=findViewById(R.id.hospital_message);
        recyclerView = findViewById(R.id.hospital_Recycler);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        progressBar = findViewById(R.id.hospital_spin_kit);
        context = this;
        back=findViewById(R.id.hospital_activity_back);
        Sprite circle = new Circle();
        progressBar.setIndeterminateDrawable(circle);
    }

    private void setRecycler() {

        list = new ArrayList<>();

        StringRequest data = new StringRequest(Request.Method.GET, URLHelper.GET_HOSPITALS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressBar.setVisibility(View.GONE);
                try {
                    JSONObject doctor;
                    JSONObject jsonObject = new JSONObject(response);
                    boolean status = jsonObject.getBoolean("status");

                    if (status) {

                        JSONArray jsonArray = jsonObject.getJSONArray("hospitals");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            doctor = jsonArray.getJSONObject(i);

                            String id = doctor.getString("id");
                            String name = doctor.getString("name");
                            String image = doctor.getString("image");
                            MainHospitalModel model = new MainHospitalModel(id,name, URLHelper.BASE_IMAGE + image);
                            Log.d("image",URLHelper.BASE_IMAGE + image);
                            list.add(model);
                        }
                        if (list==null || list.isEmpty()){
                            txtNotAvailable.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.INVISIBLE);
                        }
                        HomeHospitalsAdapter adapter = new HomeHospitalsAdapter(list, context);
                        recyclerView.setAdapter(adapter);


                    } else {
                 //       Toast.makeText(context, "No Data", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                String json = null;
                String Message;
                NetworkResponse response = error.networkResponse;
                if (response != null && response.data != null) {
                    try {
                        JSONObject errorObj = new JSONObject(new String(response.data));
                        if (response.getClass().equals(TimeoutError.class)) {
                            setRecycler();
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
                        setRecycler();
                    }
                }
            }
        }
        );
        Volley.newRequestQueue(context).add(data);


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
}
