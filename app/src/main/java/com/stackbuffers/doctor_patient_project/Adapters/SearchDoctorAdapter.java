package com.stackbuffers.doctor_patient_project.Adapters;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;
import com.stackbuffers.doctor_patient_project.Activities.ChatActivity;
import com.stackbuffers.doctor_patient_project.Activities.Doctor.TodayBookingsActivity;
import com.stackbuffers.doctor_patient_project.Activities.Patient.BookDoctorActivity;
import com.stackbuffers.doctor_patient_project.HelperClasses.Session;
import com.stackbuffers.doctor_patient_project.HelperClasses.SharedHelper;
import com.stackbuffers.doctor_patient_project.HelperClasses.URLHelper;
import com.stackbuffers.doctor_patient_project.HelperClasses.UserSessionManager;
import com.stackbuffers.doctor_patient_project.Models.SearchDoctorConsultantModel;
import com.stackbuffers.doctor_patient_project.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import libs.mjn.prettydialog.PrettyDialog;
import libs.mjn.prettydialog.PrettyDialogCallback;

public class SearchDoctorAdapter extends RecyclerView.Adapter<SearchDoctorAdapter.modelViewHolder> implements Filterable {

    Context context;
    public List<SearchDoctorConsultantModel> list;
    public List<SearchDoctorConsultantModel> filterList;
    FilterClassDoctor filter;
    UserSessionManager userSessionManager;
    Session session;
    ProgressDialog progressDialog;

    public SearchDoctorAdapter(Context context, List<SearchDoctorConsultantModel> list) {
        this.context = context;
        this.filterList = list;
        this.list = list;
        userSessionManager = new UserSessionManager(context);
        session = userSessionManager.getSessionDetails();
        progressDialog=new ProgressDialog(context);
    }

    @NonNull
    @Override
    public modelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.search_doctor_item_layout, parent, false);
        return new modelViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull modelViewHolder holder, final int i) {
        holder.name.setText("Dr. " + list.get(i).getName());
        holder.specialization.setText(list.get(i).getSpecialization());
        Picasso.get().load(list.get(i).getImage()).into(holder.image);
        holder.bookdoctor.setText(list.get(i).getType());
        holder.bookdoctor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (list.get(i).getType().equals(context.getResources().getString(R.string.contactnow))) {

                    SendEmergencyRequest(list.get(i).getId());
                } else {
                    SharedHelper sharedHelper = new SharedHelper();
                    sharedHelper.putKey(context, "did", list.get(i).getId());
                    SendUserToDoctorDetailsActivity();
                }

            }
        });
        holder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context.getApplicationContext(), ChatActivity.class);
                intent.putExtra("user_type","user");
                intent.putExtra("receiverid",list.get(i).getId());
                intent.putExtra("receivername",list.get(i).getName());
                intent.putExtra("receiverImage",list.get(i).getImage());
                intent.putExtra("token",list.get(i).getToken());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
    }

    private void SendEmergencyRequest(final String id) {
        Toast.makeText(context, "Please Wait", Toast.LENGTH_SHORT).show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLHelper.REGISTER_EMERGENCY, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

              //  Toast.makeText(context, response, Toast.LENGTH_SHORT).show();

                try {
                    JSONObject object = new JSONObject(response);
                    boolean status = object.getBoolean("status");
                    if (status) {
                        Toast.makeText(context, "Send Yor Request Please Wait.", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(context, "Already Have A Request.", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show();
                SendEmergencyRequest(id);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("doc_id", id);
                map.put("user_id", session.getId());
                return map;
            }
        };
        Volley.newRequestQueue(context).add(stringRequest);

    }

    private void SendUserToDoctorDetailsActivity() {
        Intent intent = new Intent(context.getApplicationContext(), BookDoctorActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new FilterClassDoctor(this, filterList);
        }
        return filter;
    }

    public class modelViewHolder extends RecyclerView.ViewHolder {
        TextView name, specialization;
        CircleImageView image;
        TextView bookdoctor;
        CardView item;
        public modelViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.searchdoctor_name);
            specialization = itemView.findViewById(R.id.searchdoctor_special);
            image = itemView.findViewById(R.id.searchdoctor_image);
            bookdoctor = itemView.findViewById(R.id.searchdoctor_booknow);
            item=itemView.findViewById(R.id.sd_item);
        }
    }
}
