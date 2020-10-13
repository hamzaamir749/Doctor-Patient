package com.stackbuffers.doctor_patient_project.Activities.Patient;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;

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
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.squareup.picasso.Picasso;
import com.stackbuffers.doctor_patient_project.Activities.LoginActivity;
import com.stackbuffers.doctor_patient_project.Activities.Same.MessagesListActivity;
import com.stackbuffers.doctor_patient_project.Adapters.HomeAdapter;
import com.stackbuffers.doctor_patient_project.Adapters.ViewPagerAdapter;
import com.stackbuffers.doctor_patient_project.HelperClasses.GeneralData;
import com.stackbuffers.doctor_patient_project.HelperClasses.Session;
import com.stackbuffers.doctor_patient_project.HelperClasses.SharedHelper;
import com.stackbuffers.doctor_patient_project.HelperClasses.URLHelper;
import com.stackbuffers.doctor_patient_project.HelperClasses.UserSessionManager;
import com.stackbuffers.doctor_patient_project.Models.HomeModel;
import com.stackbuffers.doctor_patient_project.R;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import android.view.Menu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;
import libs.mjn.prettydialog.PrettyDialog;
import libs.mjn.prettydialog.PrettyDialogCallback;

public class HomeScreenPatientActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    //Slider Components
    ViewPagerAdapter adapter;
    ViewPager viewPager;
    TabLayout indicator;
    List<Integer> listImages;
    RecyclerView homeRecycler;
    GridLayoutManager linearLayoutManager;
    List<HomeModel> list;
    Context context;
    CircleImageView profile_image;
    TextView profile_name;
    View view;
    UserSessionManager userSessionManager;
    Session session;
    String lang="";
    ProgressBar progressBar;
    CardView btnConsultants,btnHospitals,btnDoctors,btnDoners;
    TextView txtDoctors,txtConsultants,txtHospitals;
    SwipeRefreshLayout refreshLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLocale();
        setContentView(R.layout.activity_home_screen_patient);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        Toolbar toolbar = findViewById(R.id.toolbar);
        context = this;
        indicator = (TabLayout) findViewById(R.id.indicator);
        viewPager = findViewById(R.id.flipper_layout);
        btnDoctors=findViewById(R.id.pm_doctors);
        btnConsultants=findViewById(R.id.pm_consultants);
        btnHospitals=findViewById(R.id.pm_hospitals);
        btnDoners=findViewById(R.id.pm_donors);
        txtDoctors=findViewById(R.id.pm_txtDoctors);
        txtConsultants=findViewById(R.id.pm_txtConsultants);
        txtHospitals=findViewById(R.id.pm_txtHospitals);
        refreshLayout=findViewById(R.id.pm_refresh);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getResources().getString(R.string.menu_home));
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        view = navigationView.getHeaderView(0);
        userSessionManager = new UserSessionManager(this);
        session = userSessionManager.getSessionDetails();
        SetHeader();
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SENDPATIENTTOUPDATEACTIVITY();
            }
        });
        //Slider
        listImages = new ArrayList<>();
        listImages.add(R.drawable.sliderimage1);
        listImages.add(R.drawable.sliderimage2);
        listImages.add(R.drawable.sliderimage3);
        SetSlider();


        progressBar =findViewById(R.id.patienthomescreen_spin_kit);
        Sprite circle = new Circle();
        progressBar.setIndeterminateDrawable(circle);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                SetRecycler();
                refreshLayout.setRefreshing(false);

            }
        });

        SetRecycler();
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        btnDoctors.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SearchDoctorsActivity.class));
            }
        });
        btnConsultants.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SearchConsultantActivity.class));
            }
        });
        btnHospitals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), HospitalsActivity.class));
            }
        });
        btnDoners.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               startActivity(new Intent(getApplicationContext(),EmergencyDoctorsActivity.class));
            }
        });
    }
    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences sharedPreferences = getSharedPreferences("langSetting", Activity.MODE_PRIVATE);
        lang = sharedPreferences.getString("my_lang", "en");

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.dots_patient_cLanguage) {
            final PrettyDialog prettyDialog=new PrettyDialog(this);
            prettyDialog.setTitle(getResources().getString(R.string.ChangeLanguage))
                    .setIcon(R.drawable.ic_done).addButton(getResources().getString(R.string.English), R.color.colorWhite, R.color.colorPrimary, new PrettyDialogCallback() {
                @Override
                public void onClick() {
                    setLocale("en");
                    recreate();
                    prettyDialog.dismiss();
                }
            }).addButton(getResources().getString(R.string.Arabic), R.color.colorWhite, R.color.colorPrimary, new PrettyDialogCallback() {
                @Override
                public void onClick() {
                    setLocale("ar");
                    recreate();
                    prettyDialog.dismiss();
                }
            }).show();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    private void setLocale(String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.JELLY_BEAN_MR1)
        {
            config.setLocale(locale);
        }
        else
        {
            config.locale = locale;
        }
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        SharedPreferences.Editor editor = getSharedPreferences("langSetting", MODE_PRIVATE).edit();
        editor.putString("my_lang", language);
        editor.apply();
    }

    private void loadLocale() {
        SharedPreferences sharedPreferences = getSharedPreferences("langSetting", Activity.MODE_PRIVATE);
        lang = sharedPreferences.getString("my_lang", "en");
        setLocale(lang);

    }
    private void SetHeader() {
        profile_image = view.findViewById(R.id.homepatient_nav_imageView);
        profile_name = view.findViewById(R.id.homepatient_nav_name);
        Picasso.get().load(session.getImage()).placeholder(R.drawable.add_profile).into(profile_image);
        profile_name.setText(session.getName());
    }

    private void SENDPATIENTTOUPDATEACTIVITY() {
        Intent intent=new Intent(getApplicationContext(), UpdatePatientProfile.class);
        intent.putExtra("typeuser","user");
        startActivity(intent);
    }



    private void SetSlider() {

        adapter = new ViewPagerAdapter(HomeScreenPatientActivity.this, listImages);
        viewPager.setAdapter(adapter);
        indicator.setupWithViewPager(viewPager, true);
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new SliderTimer(), 4000, 6000);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home_screen_patient, menu);
        return true;
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_bookings) {
            sendUserToBookingMenuActivity();
        } else if (id == R.id.nav_consultation) {
            sendUserToConsultationMenuActivity();
        }  else if (id == R.id.nav_contact_us) {
            navContactUs();
        } else if (id == R.id.nav_share) {
            navShare();
        } else if (id == R.id.nav_emergency) {
            navEmergency();
        }else if (id == R.id.nav_home) {
            navHome();
        } else if (id == R.id.nav_logout) {
            navLogOut();
        }else if (id==R.id.nav_messages){
            Intent intent=new Intent(getApplicationContext(), MessagesListActivity.class);
            intent.putExtra("request","user");
            startActivity(intent);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;

    }

    private void navEmergency() {
        startActivity(new Intent(getApplicationContext(), EmergencyHelpStatusActivity.class));
    }

    private void sendUserToBookingMenuActivity() {
        startActivity(new Intent(getApplicationContext(), BookingMenuActivity.class));
    }

    private void sendUserToConsultationMenuActivity() {
        startActivity(new Intent(getApplicationContext(), ConsultationMenuActivity.class));
    }

    private void navLogOut() {
        userSessionManager.setLoggedIn(false);
        userSessionManager.clearSessionData();

        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        finishAffinity();
    }

    private void navContactUs() {
        startActivity(new Intent(getApplicationContext(), ContactUsActivity.class));
    }

    private void navHome() {
        startActivity(new Intent(getApplicationContext(), HomeScreenPatientActivity.class));
        finishAffinity();
    }

    private void navShare() {
        Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.setType("text/plain");
        share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

        // Add data to the intent, the receiving app will decide
        // what to do with it.
        share.putExtra(Intent.EXTRA_SUBJECT, "Share");
        share.putExtra(Intent.EXTRA_TEXT, "http://www.google.com");

        startActivity(Intent.createChooser(share, "Share link!"));
    }

    private void sendUserToUsersSameActivity(String bookings_here) {
        new SharedHelper().putKey(context, "activitytxt", bookings_here);
        Intent intent = new Intent(getApplicationContext(), ApprovedDoctorsConsultationsActivity.class);
        startActivity(intent);
    }

    private class SliderTimer extends TimerTask {
        @Override
        public void run() {
            HomeScreenPatientActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (viewPager.getCurrentItem() < listImages.size() - 1) {
                        viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
                    } else {
                        viewPager.setCurrentItem(0);
                    }
                }
            });
        }
    }

    private void SetRecycler() {
        progressBar.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLHelper.GET_USER_DASHBOARD, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressBar.setVisibility(View.GONE);
                try {

                    JSONObject jsonObject = new JSONObject(response);
                    boolean status = jsonObject.getBoolean("status");
                    if (status) {
                        String total_doc=jsonObject.getString("doc_count");
                        String total_cons=jsonObject.getString("cons_count");
                        String total_hosp=jsonObject.getString("hosp_count");

                        txtDoctors.setText(total_doc+" "+getResources().getString(R.string.Doctorss));
                        txtConsultants.setText(total_cons+" "+getResources().getString(R.string.Consultantss));
                        txtHospitals.setText(total_hosp+" "+getResources().getString(R.string.Hospitalss));


                    } else {
                     //   Toast.makeText(context, "Status: false", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                  //  Toast.makeText(context, "JSON EXCEPTION: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
