package com.stackbuffers.doctor_patient_project.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
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
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.rilixtech.widget.countrycodepicker.Country;
import com.rilixtech.widget.countrycodepicker.CountryCodePicker;
import com.stackbuffers.doctor_patient_project.Activities.Doctor.HomeScreenDoctorActivity;
import com.stackbuffers.doctor_patient_project.Activities.Patient.HomeScreenPatientActivity;
import com.stackbuffers.doctor_patient_project.Activities.Same.ForgetPasswordActivity;
import com.stackbuffers.doctor_patient_project.Activities.Same.PhoneAuthActivity;
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
import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {
    ImageView back;
    LinearLayout gmail;
    String type = "";
    String cpcode = "";
    LoginButton fb;
    Button btn_login;
    TextView btn_createAccount,btn_ForgetPassword;
    EditText edt_phoneNo, edt_password;
    SharedHelper sharedHelper;
    CountryCodePicker codePicker;
    ProgressDialog progressDialog;
    String password;
    RadioButton rbPatient;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;

    private static final int RC_SIGN_IN = 99;
    private CallbackManager mCallbackManager;

    Context context;
    Session session;
    UserSessionManager userSessionManager;
    String token = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        initViews();
        btn_ForgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ForgetPasswordActivity.class));
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
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edt_phoneNo.getText().toString().isEmpty() && edt_password.getText().toString().isEmpty()) {
                    edt_phoneNo.setError("Please Enter Phone Number");
                    edt_password.setError("Please Enter Password");
                } else {
                    //  sharedHelper.putKey(LoginActivity.this,"phoneno",codePicker.getDefaultCountryCodeWithPlus()+edt_phoneNo.getText().toString());
                   LoginUser();


                }
            }
        });

        LoginManager.getInstance().logOut();
        gmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
                if (acct != null) {
                    mGoogleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            //  Toast.makeText(MainActivity.this, "Signed out successfully", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });
        fb.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d("FB", "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d("FB", "facebook:onCancel");
                // ...
            }

            @Override
            public void onError(FacebookException error) {
                Log.d("FB", "facebook:onError", error);
                // ...
            }
        });

        btn_createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
                intent.putExtra("type", "normal");
                startActivity(intent);
            }
        });
/*
        cpcode = codePicker.getDefaultCountryCode();
        codePicker.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected(Country selectedCountry) {
                cpcode = selectedCountry.getPhoneCode();
            }
        });*/
    }

    private void LoginUser() {
        progressDialog.setTitle("Please Wait....");
        progressDialog.setMessage("Checking Your Account");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        if (rbPatient.isChecked()) {
            type = "user";
        } else {
            type = "doctor";
        }

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

                        session = new Session(id, email, name, phone, URLHelper.BASE_IMAGE + image, type);
                        userSessionManager.setSessionDetails(session);
                        userSessionManager.setLoggedIn(true);
                        if (rbPatient.isChecked()) {
                            SendUserToHomeActivity();
                        } else {
                            SendDoctorToHomeActivity();
                        }

                    } else {
                        Toast.makeText(LoginActivity.this, "Invalid Email Or Password", Toast.LENGTH_SHORT).show();
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
                map.put("email", edt_phoneNo.getText().toString());
                map.put("password", edt_password.getText().toString());
                map.put("type", type);
                map.put("device_type","android");
                map.put("token", token);
                return map;
            }
        };
        Volley.newRequestQueue(LoginActivity.this).add(data);

    }

    private void initViews() {
        btn_ForgetPassword=findViewById(R.id.login_forgetPassword);
        btn_login = findViewById(R.id.login_btnLogin);
        btn_createAccount = findViewById(R.id.login_createAccount);
        edt_phoneNo = findViewById(R.id.login_userNo);
        codePicker = findViewById(R.id.login_cpp);
        sharedHelper = new SharedHelper();
        progressDialog = new ProgressDialog(this);
        edt_password = findViewById(R.id.login_userPassword);
        rbPatient = findViewById(R.id.login_rbPatient);
        fb = findViewById(R.id.login_fb);
        gmail = findViewById(R.id.login_gmail);
        userSessionManager = new UserSessionManager(this);


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.google_id_gso))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mAuth = FirebaseAuth.getInstance();
        mCallbackManager = CallbackManager.Factory.create();
        fb.setReadPermissions("email", "public_profile");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                // ...
            }
        }
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {

                            Toast.makeText(LoginActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }

                        // ...
                    }
                });
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d("token", "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("token", "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("tokenfail", "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed." + task.getException(),
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // ...
                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        if (acct != null) {
            mGoogleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    //  Toast.makeText(MainActivity.this, "Signed out successfully", Toast.LENGTH_SHORT).show();
                }
            });
        }

        String name = "", email = "", image = "";
        name = user.getDisplayName();
        email = user.getEmail();
        image = user.getPhotoUrl().toString();

        Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
        intent.putExtra("namee", name);
        intent.putExtra("emaill", email);
        intent.putExtra("imagee", image);
        intent.putExtra("type", "fg");
        startActivity(intent);
        mAuth.signOut();

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

    private void SendDoctorToHomeActivity() {
        Intent intent = new Intent(getApplicationContext(), HomeScreenDoctorActivity.class);
        startActivity(intent);
        finishAffinity();
    }
}
