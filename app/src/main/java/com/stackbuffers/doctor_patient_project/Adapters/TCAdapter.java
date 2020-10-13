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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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
import com.squareup.picasso.Picasso;
import com.stackbuffers.doctor_patient_project.Activities.Doctor.TodayConsultationsActivity;
import com.stackbuffers.doctor_patient_project.Activities.Same.VideoChatViewActivity;
import com.stackbuffers.doctor_patient_project.Activities.Same.VoiceChatViewActivity;
import com.stackbuffers.doctor_patient_project.HelperClasses.GeneralData;
import com.stackbuffers.doctor_patient_project.HelperClasses.Session;
import com.stackbuffers.doctor_patient_project.HelperClasses.URLHelper;
import com.stackbuffers.doctor_patient_project.HelperClasses.UserSessionManager;
import com.stackbuffers.doctor_patient_project.Models.TCModel;
import com.stackbuffers.doctor_patient_project.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import libs.mjn.prettydialog.PrettyDialog;
import libs.mjn.prettydialog.PrettyDialogCallback;

public class TCAdapter extends RecyclerView.Adapter<TCAdapter.modelViewHolder> {

    List<TCModel> list;
    Context context;
    AlertDialog dialogBuilder;
    UserSessionManager userSessionManager;
    Session session;
    String token, channel;
    String type;

    public TCAdapter(List<TCModel> list, Context context) {
        this.list = list;
        this.context = context;
        userSessionManager = new UserSessionManager(context);
        session = userSessionManager.getSessionDetails();
    }

    @NonNull
    @Override
    public modelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.today_consultations_item_layout, parent, false);
        return new modelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull modelViewHolder holder, final int position) {
        holder.name.setText(list.get(position).getName());
        holder.time.setText(list.get(position).getTime());
        holder.date.setText(list.get(position).getDate());
        Picasso.get().load(list.get(position).getImage()).into(holder.imageView);

        final String aid = list.get(position).getId();
        holder.checked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PACC("Completed", "", list.get(position).getId());
            }
        });
        holder.videocall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (list.get(position).getToken().equals("") && list.get(position).getChannel().equals("")) {
                    Toast.makeText(context, "Token and Channel Not Available", Toast.LENGTH_SHORT).show();
                } else {

                    final PrettyDialog pDialog = new PrettyDialog(context);
                    pDialog
                            .setIcon(R.drawable.ic_local_phone_black_24dp)
                            .setTitle("Select Call")
                            .addButton(
                                    "Voice",
                                    R.color.pdlg_color_white,
                                    R.color.pdlg_color_red,
                                    new PrettyDialogCallback() {
                                        @Override
                                        public void onClick() {
                                            type="voice";
                                            pDialog.dismiss();
                                            token=list.get(position).getToken();
                                            channel=list.get(position).getChannel();
                                            CheckUserTimeForCall(list.get(position).getAppId());
                                        }
                                    }
                            ) .addButton(
                            "Video",
                            R.color.pdlg_color_white,
                            R.color.pdlg_color_red,
                            new PrettyDialogCallback() {
                                @Override
                                public void onClick() {
                                    type="video";
                                    pDialog.dismiss();
                                    token=list.get(position).getToken();
                                    channel=list.get(position).getChannel();
                                    CheckUserTimeForCall(list.get(position).getAppId());
                                }
                            }
                    )
                            .show();
                }


            }
        });
        holder.cancelled.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopUp(list.get(position).getId());
            }
        });
    }

    private void CheckUserTimeForCall(final String id) {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat dDate = new SimpleDateFormat("yyyy-MM-dd");
        final String formattedDate = dDate.format(c.getTime());
        Calendar cTime = Calendar.getInstance();
        SimpleDateFormat dTime = new SimpleDateFormat("HH:mm");
        final String formattedTime = dTime.format(cTime.getTime());

        final ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Please Wait");
        progressDialog.setTitle("Checking Time");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLHelper.CHECK_CONSULTATION_TIME, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean status = jsonObject.getBoolean("status");
                    if (status) {
                     if(type.equals("voice")){
                         Intent intent = new Intent(context, VoiceChatViewActivity.class);
                         intent.putExtra("tokenn",token);
                         intent.putExtra("channell",channel);
                         intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                         context.startActivity(intent);
                     }
                     else {
                         Intent intent = new Intent(context, VideoChatViewActivity.class);
                         intent.putExtra("tokenn",token);
                         intent.putExtra("channell",channel);
                         intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                         context.startActivity(intent);
                     }
                    } else {
                         Toast.makeText(context, "False", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                     Toast.makeText(context, "JSON EXCEPTION: "+e.getMessage(), Toast.LENGTH_SHORT).show();
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
                            CheckUserTimeForCall(id);
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
                        CheckUserTimeForCall(id);
                    }
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("user_id", id);
                map.put("doctor_id", session.getId());
                map.put("date", formattedDate);
                map.put("time", formattedTime);
                map.put("type","doctor");
                map.put("call_type",type);
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
                PACC("Cancelled", descrition.getText().toString(), id);
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
                                .setTitle(context.getResources().getString(R.string.Status))
                                .setMessage(context.getResources().getString(R.string.submitted_successfully))
                                .addButton(
                                        context.getResources().getString(R.string.ok),
                                        R.color.pdlg_color_white,
                                        R.color.pdlg_color_red,
                                        new PrettyDialogCallback() {
                                            @Override
                                            public void onClick() {
                                                pDialog.dismiss();
                                                Intent intent = new Intent(context, TodayConsultationsActivity.class);

                                                context.startActivity(intent);
                                                ((Activity) context).finish();

                                            }
                                        }
                                )
                                .show();
                    }
                } catch (JSONException e) {


                    Toast.makeText(context, "Exception: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                PACC(statusmessage, Messagecancel, appointmentid);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("status", statusmessage);
                map.put("message", Messagecancel);
                map.put("id", appointmentid);
                map.put("type", "consultation");
                return map;
            }
        };
        Volley.newRequestQueue(context).add(stringRequest);
    }

    public class modelViewHolder extends RecyclerView.ViewHolder {
        TextView name, date, time;
        CircleImageView imageView;
        Button checked, cancelled;
        ImageView videocall;

        public modelViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.todayconsultation_name);
            date = itemView.findViewById(R.id.todayconsultation_date);
            time = itemView.findViewById(R.id.todayconsultation_time);
            imageView = itemView.findViewById(R.id.todayconsultation_image);
            checked = itemView.findViewById(R.id.todayconsultation_checked);
            cancelled = itemView.findViewById(R.id.todayconsultation_cancelled);
            videocall = itemView.findViewById(R.id.todayconsultation_videocall);
        }
    }
}
