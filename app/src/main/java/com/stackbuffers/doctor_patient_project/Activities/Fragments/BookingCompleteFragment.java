package com.stackbuffers.doctor_patient_project.Activities.Fragments;


import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.stackbuffers.doctor_patient_project.Adapters.BCCAdapter;
import com.stackbuffers.doctor_patient_project.HelperClasses.GeneralData;
import com.stackbuffers.doctor_patient_project.HelperClasses.Session;
import com.stackbuffers.doctor_patient_project.HelperClasses.URLHelper;
import com.stackbuffers.doctor_patient_project.HelperClasses.UserSessionManager;
import com.stackbuffers.doctor_patient_project.Models.BccModel;
import com.stackbuffers.doctor_patient_project.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class BookingCompleteFragment extends Fragment {
    Context context;
    View view;
    UserSessionManager userSessionManager;
    Session session;
    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    ProgressBar progressBar;
TextView txtNotAvailable;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_booking_complete, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.view = view;
        this.context = view.getContext();
        userSessionManager = new UserSessionManager(context);
        session = userSessionManager.getSessionDetails();
        linearLayoutManager = new LinearLayoutManager(context);
        recyclerView = view.findViewById(R.id.recycler_bookingcompletedoctor);
        recyclerView.setLayoutManager(linearLayoutManager);
        progressBar = view.findViewById(R.id.bcd_spin_kit);
        Sprite circle = new Circle();
        txtNotAvailable=view.findViewById(R.id.bcd_message);
        progressBar.setIndeterminateDrawable(circle);
        SetRecycler();
    }
    private void SetRecycler() {
        final List<BccModel> list = new ArrayList<>();
        progressBar.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLHelper.GET_COMPLETE_APPOINTMENTS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                progressBar.setVisibility(View.GONE);
                try {
                    JSONObject object;
                    JSONObject jsonObject = new JSONObject(response);
                    boolean status = jsonObject.getBoolean("status");
                    if (status) {
                        JSONArray jsonArray = jsonObject.getJSONArray("appointments");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            object = jsonArray.getJSONObject(i);
                            String image = object.getString("user_image");
                            String name = object.getString("user_name");
                            String time = object.getString("time");
                            String date = object.getString("date");
                            String hospitalName=object.getString("hosp_name");
                            BccModel model = new BccModel( name, date,time,URLHelper.BASE_IMAGE + image,hospitalName);
                            list.add(model);
                        }
                        if (list==null || list.isEmpty()){
                            txtNotAvailable.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.INVISIBLE);
                        }
                        BCCAdapter adapter = new BCCAdapter(list,context);
                        recyclerView.setAdapter(adapter);
                    } else {

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
                            SetRecycler();
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
                        SetRecycler();
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
        Volley.newRequestQueue(context).add(stringRequest);
    }

    public void displayMessage(String toastString) {
        try {
            Toast.makeText(context, "" + toastString, Toast.LENGTH_SHORT).show();
        } catch (Exception ee) {
            ee.printStackTrace();
        }
    }
}
