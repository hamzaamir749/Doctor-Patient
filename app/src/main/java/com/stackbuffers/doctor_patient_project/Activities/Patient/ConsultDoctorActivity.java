package com.stackbuffers.doctor_patient_project.Activities.Patient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import io.agora.rtc.*;


import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.TimePicker;
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
import com.stackbuffers.doctor_patient_project.HelperClasses.DatePickerFragment;
import com.stackbuffers.doctor_patient_project.HelperClasses.GeneralData;
import com.stackbuffers.doctor_patient_project.HelperClasses.Session;
import com.stackbuffers.doctor_patient_project.HelperClasses.SharedHelper;
import com.stackbuffers.doctor_patient_project.HelperClasses.TimePickerFragment;
import com.stackbuffers.doctor_patient_project.HelperClasses.URLHelper;
import com.stackbuffers.doctor_patient_project.HelperClasses.UserSessionManager;
import com.stackbuffers.doctor_patient_project.Models.HospitalModel;
import com.stackbuffers.doctor_patient_project.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import io.agora.rtc.RtcEngine;

public class ConsultDoctorActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    Button btn_chooseDate,btn_chooseTime;
    String Date = "",Time="";
    Context context;
    ProgressBar progressBar;
    CircleImageView img_imageView;
    SharedHelper sharedHelper;
    ProgressDialog progressDialog;
    Button btn_booknow;
    TextView txt_name, txt_fee, txt_specility, txt_days, txt_time;
    Spinner spinnerHospital;
    HospitalModel model;
    HospitalModel selectedHospital;
    List<HospitalModel> list;
    ImageView back;
    int hospitalID = 0;

    LinearLayout lnr_days;
    String doctorID;
    UserSessionManager userSessionManager;
    Session session;
    String user_id;
    RadioGroup radioGroup;
    String c_fee, h_fee;
    String token="",channelName="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consult_doctor);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        initViews();
        getDetailsOfDoctors();
        SetDaysAndTimeconsult();
        btn_chooseDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PickDate();
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        btn_chooseTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              PickTime();
            }
        });
/*        findViewById(R.id.consultdoctor_rbConsultOnline).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txt_fee.setText(c_fee);
                consultation=1;
                SetDaysAndTimeconsult();
                spinnerHospital.setVisibility(View.GONE);
            }
        });
        findViewById(R.id.consultdoctor_rbDoctor).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txt_fee.setText(h_fee);
                consultation=0;
                getHospitals();
                spinnerHospital.setVisibility(View.VISIBLE);

            }
        });*/
        btn_booknow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Date.equals("") && Time.equals(""))
                {
                    Toast.makeText(context, "Choose Date And Time", Toast.LENGTH_SHORT).show();
                }
                else if (Date.equals(""))
                {
                    Toast.makeText(context, "Choose Date", Toast.LENGTH_SHORT).show();
                }
                else if (Time.equals("")){
                    Toast.makeText(context, "Choose Time", Toast.LENGTH_SHORT).show();
                }
                else {
                    BookAppointment();
                }
            }
        });
    }

    private void PickTime() {
        DialogFragment dialogFragment=new TimePickerFragment();
        dialogFragment.show(getSupportFragmentManager(),"Pick Time");
    }


    private void getDetailsOfDoctors() {
        progressBar.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLHelper.GET_DOCTOR_DETAILS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressBar.setVisibility(View.GONE);
                try {

                    JSONObject jsonObject = new JSONObject(response);
                    boolean status = jsonObject.getBoolean("status");
                    if (status) {
                        String name = jsonObject.getString("name");
                        String speciality = jsonObject.getString("specialities");
                        h_fee = jsonObject.getString("fee");
                        String image = jsonObject.getString("image");
                        txt_name.setText(name);
                        txt_specility.setText(speciality);
                        Picasso.get().load(URLHelper.BASE_IMAGE + image).into(img_imageView);

                    } else {
              //          Toast.makeText(context, "No Details", Toast.LENGTH_SHORT).show();
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
                            getDetailsOfDoctors();
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
                        getDetailsOfDoctors();
                    }
                }
            }

        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("id", doctorID);
                return map;
            }
        };
        Volley.newRequestQueue(context).add(stringRequest);
    }

    private void getHospitals() {
        progressBar.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLHelper.GET_DOCTOR_DDETAILS_HOSPITALS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressBar.setVisibility(View.GONE);
                try {
                    JSONObject jobj;
                    JSONObject object = new JSONObject(response);
                    boolean status = object.getBoolean("status");
                    if (status) {

                        JSONArray jsonArray = object.getJSONArray("hospitals");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            jobj = jsonArray.getJSONObject(i);
                            int cityid = jobj.getInt("id");
                            String cityn = jobj.getString("name");
                            model = new HospitalModel(cityn, cityid);
                            list.add(model);
                        }

                        setHospitalSpinner();
                    }
                } catch (JSONException e) {
                //    Toast.makeText(context, "JSON Exception Hospitals: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
                            getHospitals();
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
                        getHospitals();
                    }
                }
            }

        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("id", doctorID);
                return map;
            }
        };
        Volley.newRequestQueue(context).add(stringRequest);
    }

    void setHospitalSpinner() {

        SpinnerAdapter spinnerAdapter;
        List<String> cities = getHospitalsForSpinner();

        spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, cities);
        spinnerHospital.setAdapter(spinnerAdapter);
        progressBar.setVisibility(View.GONE);
        spinnerHospital.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String tvSelectedHospitals = parent.getItemAtPosition(position).toString();
                if (tvSelectedHospitals.equals("Select Hospital")) {
                    selectedHospital = null;
                    hospitalID = 0;
                    return;
                }
                for (HospitalModel item : list) {
                    if (item.getName().equals(tvSelectedHospitals)) {
                        selectedHospital = item;
                    }
                }

                hospitalID = selectedHospital.getId();
                SetDaysAndTime();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void SetDaysAndTime() {
        progressDialog.setMessage("Getting Data");
        progressDialog.setTitle("Please Wait");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLHelper.GET_DAYS_TIME, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                lnr_days.setVisibility(View.VISIBLE);
             //   Toast.makeText(context, response, Toast.LENGTH_SHORT).show();
                lnr_days.setVisibility(View.VISIBLE);
                try {
                    boolean bookingStatus = false;
                    JSONObject jobj;
                    JSONObject object = new JSONObject(response);
                    boolean status = object.getBoolean("status");
                    if (status) {

                        String days = object.getString("days");
                        String to = object.getString("to");
                        String from = object.getString("from");
                        bookingStatus = object.getBoolean("booking");

                        if (bookingStatus) {
                            btn_booknow.setVisibility(View.VISIBLE);
                        } else {
                            btn_booknow.setVisibility(View.GONE);
                        }

                        txt_days.setText(days + " Only From " + from + " To " + to);
                    }
                } catch (JSONException e) {
                //    Toast.makeText(context, "JSON Exception Hospitals: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
                            SetDaysAndTime();
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
                        SetDaysAndTime();
                    }
                }
            }

        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("doc_id", doctorID);
                map.put("hosp_id", String.valueOf(hospitalID));
                map.put("user_id", String.valueOf(session.getId()));
                return map;
            }
        };
        Volley.newRequestQueue(context).add(stringRequest);
    }

    private void BookAppointment() {

        progressDialog.setMessage("Booking Consultation");
        progressDialog.setTitle("Please Wait");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLHelper.BOOK_DOCTOR_CONSULATATION, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
           //     Toast.makeText(context, response, Toast.LENGTH_SHORT).show();
                lnr_days.setVisibility(View.VISIBLE);
                try {
                    boolean bookingStatus = false;
                    JSONObject jobj;
                    JSONObject object = new JSONObject(response);
                    boolean status = object.getBoolean("status");
                    if (status) {
                        startActivity(new Intent(getApplicationContext(), HomeScreenPatientActivity.class));
                        finishAffinity();
                    } else {
                    Toast.makeText(context, "Try Again", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                   Toast.makeText(context, "JSON Exception: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
                            BookAppointment();
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
                        BookAppointment();
                    }
                }
            }

        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("doc_id", doctorID);
                map.put("hosp_id", String.valueOf(hospitalID));
                map.put("user_id", String.valueOf(session.getId()));
                map.put("date", Date);
                map.put("time", Time);
                map.put("consultation","1");
                return map;
            }
        };
        Volley.newRequestQueue(context).add(stringRequest);

    }

    private void SetDaysAndTimeconsult() {
        progressBar.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLHelper.GET_DAYS_TIME2, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressBar.setVisibility(View.GONE);
                try {
                    boolean bookingStatus = false;
                    JSONObject jobj;
                    JSONObject object = new JSONObject(response);
                    boolean status = object.getBoolean("status");
               //     Toast.makeText(context, "status: " + status, Toast.LENGTH_SHORT).show();
                    if (status) {
                        c_fee = object.getString("c_fee");
                        String days = object.getString("days");
                        String to = object.getString("to");
                        String from = object.getString("from");
                        bookingStatus = object.getBoolean("booking");

                        if (bookingStatus) {
                            btn_booknow.setVisibility(View.VISIBLE);
                        } else {
                            btn_booknow.setVisibility(View.GONE);
                        }
                        txt_fee.setText(c_fee);
                        txt_days.setText(days + " Only From " + from + " To " + to);
                    }
                } catch (JSONException e) {
               //     Toast.makeText(context, "JSON Exception Days Time: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
                            SetDaysAndTimeconsult();
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
                        SetDaysAndTimeconsult();
                    }
                }
            }

        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("doc_id", doctorID);
                map.put("user_id", String.valueOf(session.getId()));
                return map;
            }
        };
        Volley.newRequestQueue(context).add(stringRequest);
    }

    private List<String> getHospitalsForSpinner() {
        List<String> hospitals = new ArrayList<>();
        hospitals.add("Select Hospital");
        for (HospitalModel item : list) {
            hospitals.add(item.getName());
        }
        return hospitals;
    }

    private void PickDate() {
        DialogFragment dialogFragment = new DatePickerFragment();
        dialogFragment.show(getSupportFragmentManager(), "Pick Date");
    }

    private void initViews() {
        btn_chooseDate = findViewById(R.id.consultdoctor_choosedate);
        btn_chooseTime = findViewById(R.id.consultdoctor_choosetime);
        spinnerHospital = findViewById(R.id.consultdoctor_spinnerhospital);
        txt_name = findViewById(R.id.consultdoctor_name);
        txt_specility = findViewById(R.id.consultdoctor_speciality);
        txt_fee = findViewById(R.id.consultdoctor_fee);
        txt_days = findViewById(R.id.consultdoctor_days);
        btn_booknow = findViewById(R.id.consultdoctor_Booknow);
        progressDialog = new ProgressDialog(this);
        progressBar = findViewById(R.id.consultdoctor_spin_kit);
        img_imageView = findViewById(R.id.consultdoctor_profileimage);
        back = findViewById(R.id.consultdoctor_back);
        lnr_days = findViewById(R.id.consultdoctor_availabledaysetc);
        radioGroup = findViewById(R.id.consultdoctor_radioGroup);
        Sprite circle = new Circle();
        progressBar.setIndeterminateDrawable(circle);
        context = this;
        list = new ArrayList<>();
        sharedHelper = new SharedHelper();
        doctorID = sharedHelper.getKey(context, "cid");
        userSessionManager = new UserSessionManager(context);
        session = userSessionManager.getSessionDetails();

    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

        String monthstr = String.valueOf(month + 1);
        String day=String.valueOf(dayOfMonth);
        if (monthstr.length()==1)
        {
            monthstr="0"+monthstr;
        }
        if (day.length()==1)
        {
            day="0"+dayOfMonth;
        }

         Date=String.valueOf(year)+"-"+monthstr+"-"+day;



        btn_chooseDate.setText(Date);

    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        list.clear();
        super.onDestroy();
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

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        String mintt=String.valueOf(minute);
        String hour=String.valueOf(hourOfDay);
        if (mintt.length()==1)
        {
            mintt="0"+mintt;
        }
        if (hour.length()==1)
        {
            hour="0"+hour;
        }
        Time=hour+":"+mintt;
        btn_chooseTime.setText(Time);
    }
}
