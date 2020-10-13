package com.stackbuffers.doctor_patient_project.Activities.Patient;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.google.android.material.snackbar.Snackbar;
import com.rilixtech.widget.countrycodepicker.CountryCodePicker;
import com.stackbuffers.doctor_patient_project.HelperClasses.GeneralData;
import com.stackbuffers.doctor_patient_project.HelperClasses.URLHelper;
import com.stackbuffers.doctor_patient_project.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ContactUsActivity extends AppCompatActivity {
    CountryCodePicker codePicker;
    EditText edt_name, edt_email, edt_phone, edt_message;
    Button btn_submit;
    ProgressDialog progressDialog;
    ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        initViews();
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edt_name.getText().toString().isEmpty() && edt_email.getText().toString().isEmpty() && edt_phone.getText().toString().isEmpty()) {
                    edt_name.setError("Please Enter Here");
                    edt_email.setError("Please Enter Here");
                    edt_phone.setError("Please Enter Here");
                } else if (edt_name.getText().toString().isEmpty()) {
                    edt_name.setError("Please Enter Here");
                } else if (edt_email.getText().toString().isEmpty()) {
                    edt_email.setError("Please Enter Here");
                } else if (edt_phone.getText().toString().isEmpty()) {
                    edt_phone.setError("Please Enter Here");
                } else {
                    SaveContactUsData();
                }

            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void initViews() {
        edt_email = findViewById(R.id.contactus_email);
        edt_name = findViewById(R.id.contactus_name);
        edt_phone = findViewById(R.id.contactus_phone);
        edt_message = findViewById(R.id.contactus_message);
        btn_submit = findViewById(R.id.contactus_submit);
        back = findViewById(R.id.contactus_back);
        codePicker = findViewById(R.id.contactus_cpp);
        progressDialog = new ProgressDialog(this);
    }

    private void SaveContactUsData() {
        progressDialog.setTitle("Please Wait....");
        progressDialog.setMessage("Submitting Your Data");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        StringRequest data = new StringRequest(Request.Method.POST, URLHelper.CONTACTUS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean status = jsonObject.getBoolean("status");
                    if (status) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(ContactUsActivity.this);
                        builder.setMessage(R.string.submitted_successfully)
                                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        SendUserToHomeActivity();
                                    }
                                });
                        builder.show();
                    } else {
                     //   Toast.makeText(ContactUsActivity.this, "Not Submitted", Toast.LENGTH_SHORT).show();
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
                            SaveContactUsData();
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
                        SaveContactUsData();
                    }
                }
            }
        }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("name", edt_name.getText().toString());
                map.put("email", edt_email.getText().toString());
                map.put("phone", codePicker.getDefaultCountryCodeWithPlus() + edt_phone.getText().toString());
                map.put("message", edt_message.getText().toString());
                return map;
            }
        };
        Volley.newRequestQueue(ContactUsActivity.this).add(data);

    }

    private void SendUserToHomeActivity() {

        startActivity(new Intent(getApplicationContext(), HomeScreenPatientActivity.class));
        finish();

    }

    public void displayMessage(String toastString) {
        try {
            Snackbar.make(getCurrentFocus(), toastString, Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show();
        } catch (Exception e) {
            try {
                Toast.makeText(ContactUsActivity.this, "" + toastString, Toast.LENGTH_SHORT).show();
            } catch (Exception ee) {
                ee.printStackTrace();
            }
        }
    }
}
