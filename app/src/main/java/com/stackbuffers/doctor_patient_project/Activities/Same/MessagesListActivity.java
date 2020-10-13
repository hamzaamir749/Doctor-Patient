package com.stackbuffers.doctor_patient_project.Activities.Same;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.stackbuffers.doctor_patient_project.Adapters.MessagesListAdapter;
import com.stackbuffers.doctor_patient_project.HelperClasses.Session;
import com.stackbuffers.doctor_patient_project.HelperClasses.URLHelper;
import com.stackbuffers.doctor_patient_project.HelperClasses.UserSessionManager;
import com.stackbuffers.doctor_patient_project.Models.MessagesModel;
import com.stackbuffers.doctor_patient_project.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessagesListActivity extends AppCompatActivity {
    String request_type;
    RecyclerView recyclerView;
    List<MessagesModel> list;
    UserSessionManager userSessionManager;
    Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages_list);
        request_type = getIntent().getStringExtra("request");
        initViews();
        getData();
    }

    private void getData() {
        list = new ArrayList<>();
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLHelper.GET_MESSAGES, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONObject object = null;
                    boolean status = jsonObject.getBoolean("status");
                    if (status) {
                        JSONArray jsonArray = jsonObject.getJSONArray("users");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            object = jsonArray.getJSONObject(i);
                            list.add(new MessagesModel(object.getString("id"), object.getString("name"), URLHelper.BASE_IMAGE + object.getString("image"), object.getString("token"), request_type));
                        }

                        MessagesListAdapter adapter = new MessagesListAdapter(list, MessagesListActivity.this);
                        recyclerView.setAdapter(adapter);

                    }
                } catch (JSONException e) {
                    Toast.makeText(MessagesListActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MessagesListActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
                getData();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("user_id", session.getId());
                map.put("type", request_type);
                return map;
            }
        };
        Volley.newRequestQueue(MessagesListActivity.this).add(stringRequest);
    }

    private void initViews() {

        getSupportActionBar().setTitle("Messages");
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        } else {
            getActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getActionBar().setHomeButtonEnabled(true);
        }

        recyclerView = findViewById(R.id.messageslist_all);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        userSessionManager = new UserSessionManager(MessagesListActivity.this);
        session = userSessionManager.getSessionDetails();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
