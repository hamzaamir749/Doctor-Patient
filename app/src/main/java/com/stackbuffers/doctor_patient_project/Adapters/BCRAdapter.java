package com.stackbuffers.doctor_patient_project.Adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;
import com.stackbuffers.doctor_patient_project.Activities.Doctor.BookingMenuDoctorActivity;
import com.stackbuffers.doctor_patient_project.Activities.Doctor.ConsultationMenuDoctorActivity;
import com.stackbuffers.doctor_patient_project.HelperClasses.URLHelper;
import com.stackbuffers.doctor_patient_project.Models.BcrModel;
import com.stackbuffers.doctor_patient_project.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import libs.mjn.prettydialog.PrettyDialog;
import libs.mjn.prettydialog.PrettyDialogCallback;

public class BCRAdapter extends RecyclerView.Adapter<BCRAdapter.modelViewHolder> {
    List<BcrModel> list;
    Context context;
    AlertDialog dialogBuilder;
    boolean act=false;
    String typeActivity="";

    public BCRAdapter(List<BcrModel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public modelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.bookingconsultation_requests_layout, parent, false);
        return new modelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull modelViewHolder holder, final int position) {
        holder.name.setText(list.get(position).getName());
        holder.time.setText(list.get(position).getTime());
        holder.date.setText(list.get(position).getDate());
        Picasso.get().load(list.get(position).getImage()).into(holder.imageView);

        final String aid = list.get(position).getId();
        holder.approved.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PACC("Approved", "", list.get(position).getId());

            }
        });
        holder.cancelled.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopUp(list.get(position).getId());
            }
        });
        if (list.get(position).getHospitalName().equals("")) {
            holder.hospitalName.setVisibility(View.GONE);
            act=false;
            typeActivity="consultation";
        } else {
            act=true;
            holder.hospitalName.setText(list.get(position).getHospitalName());
            typeActivity="appointment";
        }
    }

    private void PopUp(final String id) {
          dialogBuilder = new AlertDialog.Builder(context).create();
        // LayoutInflater inflater = context.getApplicationContext().getLayoutInflater();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.cancel_dialouge_item_layout, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setCanceledOnTouchOutside(true);

        final EditText descrition = dialogView.findViewById(R.id.cancel_dialouge_text);
        Button submit = dialogView.findViewById(R.id.cancel_dialouge_submit);
        dialogBuilder.show();
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PACC("Cancelled",descrition.getText().toString(),id);
                dialogBuilder.dismiss();
            }
        });
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public void PACC(final String statusmessage, final String Messagecancel, final String appointmentid) {

        final ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Please Wait");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLHelper.UpdateAppointmentConsultations, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean status = jsonObject.getBoolean("status");
                    if (status) {

                       final PrettyDialog pDialog = new PrettyDialog(context);
                        pDialog
                                .setTitle("Status")
                                .setMessage("Successfully Completed")
                                .addButton(
                                        "Ok",
                                        R.color.pdlg_color_white,
                                        R.color.pdlg_color_red,
                                        new PrettyDialogCallback() {
                                            @Override
                                            public void onClick() {
                                                pDialog.dismiss();
                                              if (act){
                                                  context.startActivity(new Intent(context,BookingMenuDoctorActivity.class));
                                                  ((Activity)context).finish();
                                              }
                                              else {
                                                  context.startActivity(new Intent(context, ConsultationMenuDoctorActivity.class));
                                                  ((Activity)context).finish();
                                              }
                                            }
                                        }
                                )
                                .show();
                    }
                } catch (JSONException e) {


                 //   Toast.makeText(context, "Exception: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                PACC(statusmessage,Messagecancel,appointmentid);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("status", statusmessage);
                map.put("message", Messagecancel);
                map.put("id", appointmentid);
                map.put("type",typeActivity);
                return map;
            }
        };
        Volley.newRequestQueue(context).add(stringRequest);
    }

    public class modelViewHolder extends RecyclerView.ViewHolder {
        TextView name, date, time, hospitalName;
        CircleImageView imageView;
        Button approved, cancelled;

        public modelViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.bc_name);
            date = itemView.findViewById(R.id.bc_date);
            time = itemView.findViewById(R.id.bc_time);
            imageView = itemView.findViewById(R.id.bc_image);
            approved = itemView.findViewById(R.id.bc_approved);
            cancelled = itemView.findViewById(R.id.bc_cancelled);
            hospitalName = itemView.findViewById(R.id.bc_hospital);

        }
    }
}
