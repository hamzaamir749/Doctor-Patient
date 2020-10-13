package com.stackbuffers.doctor_patient_project.Activities.Doctor;

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
import com.stackbuffers.doctor_patient_project.Adapters.TCAdapter;
import com.stackbuffers.doctor_patient_project.HelperClasses.GeneralData;
import com.stackbuffers.doctor_patient_project.HelperClasses.Session;
import com.stackbuffers.doctor_patient_project.HelperClasses.URLHelper;
import com.stackbuffers.doctor_patient_project.HelperClasses.UserSessionManager;
import com.stackbuffers.doctor_patient_project.Models.TBModel;
import com.stackbuffers.doctor_patient_project.Models.TCModel;
import com.stackbuffers.doctor_patient_project.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TodayConsultationsActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    List<TCModel> list=null;
    Context context;
    ProgressBar progressBar;
    UserSessionManager userSessionManager;
    Session session;
    ImageView back;
    TextView txtNotAvailable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_today_consultations);
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
        context = this;
        txtNotAvailable=findViewById(R.id.tc_message);
        recyclerView = findViewById(R.id.recycler_tc);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        userSessionManager = new UserSessionManager(context);
        session = userSessionManager.getSessionDetails();
        progressBar = findViewById(R.id.tc_spin_kit);
        back = findViewById(R.id.tc_back);

        Sprite circle = new Circle();
        progressBar.setIndeterminateDrawable(circle);
    }

    private void setRecycler() {

        list = new ArrayList<>();

        StringRequest data = new StringRequest(Request.Method.POST, URLHelper.GET_TODAY_CONSULTATIONS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressBar.setVisibility(View.GONE);
            //    Toast.makeText(context, response, Toast.LENGTH_SHORT).show();
                try {
                    JSONObject object;
                    JSONObject jsonObject = new JSONObject(response);
                    boolean status = jsonObject.getBoolean("status");

                    if (status) {

                        JSONArray jsonArray = jsonObject.getJSONArray("consultations");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            object = jsonArray.getJSONObject(i);
                            String image = object.getString("user_image");
                            String name = object.getString("user_name");
                            String time = object.getString("time");
                            String date = object.getString("date");
                            String userID = object.getString("user_id");
                            String id = object.getString("cons_id");
                            String token = object.getString("token");
                            String channel = object.getString("channel");
                            TCModel model = new TCModel(name, date, time, URLHelper.BASE_IMAGE + image, id, userID,token,channel);
                            list.add(model);
                        }
                        if (list==null || list.isEmpty()){
                            txtNotAvailable.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.INVISIBLE);
                        }
                        TCAdapter adapter = new TCAdapter(list, context);
                        recyclerView.setAdapter(adapter);


                    } else {

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
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("doctor_id", session.getId());
                return map;
            }
        };
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
