package com.stackbuffers.doctor_patient_project.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.rilixtech.widget.countrycodepicker.Country;
import com.rilixtech.widget.countrycodepicker.CountryCodePicker;
import com.squareup.picasso.Picasso;
import com.stackbuffers.doctor_patient_project.Activities.Patient.HomeScreenPatientActivity;
import com.stackbuffers.doctor_patient_project.Activities.Same.PhoneAuthActivity;
import com.stackbuffers.doctor_patient_project.HelperClasses.GeneralData;
import com.stackbuffers.doctor_patient_project.HelperClasses.Session;
import com.stackbuffers.doctor_patient_project.HelperClasses.SharedHelper;
import com.stackbuffers.doctor_patient_project.HelperClasses.URLHelper;
import com.stackbuffers.doctor_patient_project.HelperClasses.UserSessionManager;
import com.stackbuffers.doctor_patient_project.Models.Cities;
import com.stackbuffers.doctor_patient_project.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;

public class SignUpActivity extends AppCompatActivity {
    Button btn_signUp;
    Spinner CityName;
    List<Cities> cityList;
    int cityid = 0;
    ImageView back;
    private Cities selectedCity;
    RadioButton male, female;
    ProgressBar progressBar;
    CircleImageView img_imageView;
    EditText edt_name, edt_email, edt_phone, edt_password;
    CountryCodePicker cpp;
    public static int SELECT_PHOTO = 1000;
    String profile_img = "";
    Context context;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    SharedHelper sharedHelper;
    ProgressDialog progressDialog;
    String type = "";
    String emaill, namee;
    String cpcode;
    String token = "";
    Session session;
    UserSessionManager userSessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        type = getIntent().getExtras().getString("type");
        initViews();
        SetCities();
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
        if (type.equals("fg")) {
            profile_img = getIntent().getExtras().getString("imagee");
            Picasso.get().load(profile_img).placeholder(R.drawable.add_profile).into(img_imageView);
            emaill = getIntent().getExtras().getString("emaill");
            namee = getIntent().getExtras().getString("namee");
            if (!emaill.equals("")) {
                edt_email.setText(emaill);
                edt_name.setText(namee);
            }
        }

       /* cpcode = cpp.getDefaultCountryCode();
        cpp.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected(Country selectedCountry) {
                cpcode = selectedCountry.getPhoneCode();
            }
        });*/
        btn_signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignUpUser();
            }
        });
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

    }

    private void SignUpUser() {
        if (edt_name.getText().toString().isEmpty() && edt_email.getText().toString().isEmpty() && edt_phone.getText().toString().isEmpty() && edt_password.getText().toString().isEmpty()) {
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
        } else if (edt_password.getText().toString().isEmpty()) {
            edt_password.setError("Please Enter Here");
        } else if (cityid == 0) {
            Toast.makeText(this, "Please Select City", Toast.LENGTH_SHORT).show();
        } else if (profile_img.equals("")) {
            Toast.makeText(this, "Please Select Profile Image", Toast.LENGTH_SHORT).show();
        } else {

            sharedHelper.putKey(context, "signUp_image", "data:image/jpeg;base64," + profile_img);
            sharedHelper.putKey(context, "signUp_name", edt_name.getText().toString());
            sharedHelper.putKey(context, "signUp_email", edt_email.getText().toString());
            sharedHelper.putKey(context, "signUp_password", edt_password.getText().toString());
            sharedHelper.putKey(context, "signUp_phone", edt_phone.getText().toString());
            sharedHelper.putKey(context, "signUp_type", type);
            sharedHelper.putKey(context, "signUp_city", String.valueOf(cityid));
            if (male.isChecked()) {
                sharedHelper.putKey(context, "signUp_gender", "male");
            }
            if (female.isChecked()) {
                sharedHelper.putKey(context, "signUp_gender", "female");
            }

            RegisterUser();

        }

    }

    public void initViews() {
        back = findViewById(R.id.signup_back);
        btn_signUp = findViewById(R.id.signup_btnsignup);
        CityName = findViewById(R.id.signup_spinnercity);
        cityList = new ArrayList<>();
        male = findViewById(R.id.rbMale);
        female = findViewById(R.id.rbFemale);
        progressBar = findViewById(R.id.signup_spin_kit);
        edt_name = findViewById(R.id.signup_username);
        edt_email = findViewById(R.id.signup_email);
        edt_password = findViewById(R.id.signup_password);
        edt_phone = findViewById(R.id.signup_phone);
        cpp = findViewById(R.id.signup_cpp);
        img_imageView = findViewById(R.id.signup_profileimage);
        progressDialog = new ProgressDialog(this);
        sharedHelper = new SharedHelper();
        context = this;
        Sprite circle = new Circle();
        progressBar.setIndeterminateDrawable(circle);
        userSessionManager = new UserSessionManager(this);
        context = this;
    }

    private void SendUserToOTPActivity() {
        Intent phoneauthintent = new Intent(context, PhoneAuthActivity.class);
        phoneauthintent.putExtra("activityname", "signup");
        startActivity(phoneauthintent);
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


    void SetCities() {

        StringRequest stringRequest = new StringRequest(Request.Method.GET, URLHelper.GET_CITIES, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jobj;
                    JSONObject object = new JSONObject(response);
                    boolean status = object.getBoolean("status");
                    if (status) {
                        Cities newItem;
                        JSONArray jsonArray = object.getJSONArray("cities");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            jobj = jsonArray.getJSONObject(i);
                            int cityid = jobj.getInt("id");
                            String cityn = jobj.getString("name");
                            newItem = new Cities(cityid, cityn);
                            cityList.add(newItem);
                        }

                        setCitySpinner();
                    }
                } catch (JSONException e) {
                    //  Toast.makeText(SignUpActivity.this, "JSON Exception Cities: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //   Toast.makeText(SignUpActivity.this, "Volley Cities: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                SetCities();
            }
        });
        Volley.newRequestQueue(this).add(stringRequest);
    }

    void setCitySpinner() {

        SpinnerAdapter spinnerAdapter;
        List<String> cities = getCityForSpinner();

        spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, cities);
        CityName.setAdapter(spinnerAdapter);
        progressBar.setVisibility(View.GONE);
        CityName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String tvSelectedCity = parent.getItemAtPosition(position).toString();
                if (tvSelectedCity.equals("Select City")) {
                    selectedCity = null;
                    cityid = 0;
                    return;
                }
                for (Cities item : cityList) {
                    if (item.getName().equals(tvSelectedCity)) {
                        selectedCity = item;
                    }
                }

                cityid = selectedCity.getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private List<String> getCityForSpinner() {
        List<String> cities = new ArrayList<>();
        cities.add("Select City");
        for (Cities item : cityList) {
            cities.add(item.getName());
        }
        return cities;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SELECT_PHOTO && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                img_imageView.setImageBitmap(bitmap);
                profile_img = ImageToString(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String ImageToString(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
        byte[] byteFormat = stream.toByteArray();
        // get the base 64 string
        String imgString = Base64.encodeToString(byteFormat, Base64.NO_WRAP);
        return imgString;
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
                map.put("type", sharedHelper.getKey(context, "signUp_type"));
                map.put("token", token);
                map.put("device_type","android");
                return map;
            }
        };
        data.setRetryPolicy(new DefaultRetryPolicy(
                120000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(SignUpActivity.this).add(data);
    }

    private void SendUserToHomeActivity() {
        Intent intent = new Intent(getApplicationContext(), HomeScreenPatientActivity.class);
        startActivity(intent);
        finishAffinity();
    }
}
