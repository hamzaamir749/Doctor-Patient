package com.stackbuffers.doctor_patient_project.Adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.stackbuffers.doctor_patient_project.Activities.Same.VideoChatViewActivity;
import com.stackbuffers.doctor_patient_project.Activities.Same.VoiceChatViewActivity;
import com.stackbuffers.doctor_patient_project.HelperClasses.GeneralData;
import com.stackbuffers.doctor_patient_project.HelperClasses.Session;
import com.stackbuffers.doctor_patient_project.HelperClasses.URLHelper;
import com.stackbuffers.doctor_patient_project.HelperClasses.UserSessionManager;
import com.stackbuffers.doctor_patient_project.Models.ConsultationStatusModel;
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

public class ConsultationStatusAdapter extends RecyclerView.Adapter<ConsultationStatusAdapter.modelViewHolder> {
    Context context;
    List<ConsultationStatusModel> list;
    UserSessionManager userSessionManager;
    Session session;
    String token,channel;
    String type="";
    public ConsultationStatusAdapter(Context context, List<ConsultationStatusModel> list) {
        this.context = context;
        this.list = list;
        userSessionManager=new UserSessionManager(context);
        session=userSessionManager.getSessionDetails();
    }


    @NonNull
    @Override
    public modelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.consultation_menu_item_layout,parent,false);
        return new modelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull modelViewHolder holder, final int i) {
        holder.name.setText("DR. "+list.get(i).getName());

        holder.date.setText(list.get(i).getDate());
        holder.time.setText(list.get(i).getTime());
        Picasso.get().load(list.get(i).getImage()).into(holder.image);
        String status=list.get(i).getStatus();

        if (status.equals("Pending")){
            holder.status.setText(status);
        }
        else if (status.equals("Completed")){
            holder.status.setText(status);
        }else if (status.equals("Cancelled")){
            holder.status.setText(status);
        }else if (status.equals("Approved")) {
            holder.call.setVisibility(View.VISIBLE);
            holder.status.setText(status);
        }
        holder.call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (list.get(i).getToken().equals("") && list.get(i).getChannel().equals(""))
                {
                    Toast.makeText(context, "Token and Channel Not Available", Toast.LENGTH_SHORT).show();
                }
                else {
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
                                            token=list.get(i).getToken();
                                            channel=list.get(i).getChannel();
                                            CheckUserTimeForCall(list.get(i).getId());
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
                                    token=list.get(i).getToken();
                                    channel=list.get(i).getChannel();
                                    CheckUserTimeForCall(list.get(i).getId());
                                }
                            }
                    )
                            .show();

                }



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
    /*    Toast.makeText(context, formattedDate, Toast.LENGTH_SHORT).show();
        Toast.makeText(context, formattedTime, Toast.LENGTH_SHORT).show();
        Toast.makeText(context, "Doctor_id "+id, Toast.LENGTH_SHORT).show();
        Toast.makeText(context, "User_id "+session.getId(), Toast.LENGTH_SHORT).show();*/
        final ProgressDialog progressDialog=new ProgressDialog(context);
        progressDialog.setMessage("Please Wait");
        progressDialog.setTitle("Checking Time");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        StringRequest stringRequest=new StringRequest(Request.Method.POST, URLHelper.CHECK_CONSULTATION_TIME, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
             //   Toast.makeText(context, "responce: "+response, Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
                try{
                    JSONObject jsonObject=new JSONObject(response);
                    boolean status=jsonObject.getBoolean("status");
                    if (status)
                    {

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
                    }
                    else {
                     Toast.makeText(context, "Appointment Time Not Match", Toast.LENGTH_SHORT).show();
                    }
                }
                catch (JSONException e)
                {
               //     Toast.makeText(context, "JSON EXCEPTION: "+e.getMessage(), Toast.LENGTH_SHORT).show();
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
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("user_id", session.getId());
                map.put("doctor_id", id);
                map.put("date",formattedDate);
                map.put("time",formattedTime);
                map.put("type","user");
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
    @Override
    public int getItemCount() {
        return list.size();
    }

    public class modelViewHolder extends RecyclerView.ViewHolder {
        CircleImageView image;
        TextView name,hospital,date,time;
        Button status,call;
        public modelViewHolder(@NonNull View itemView) {
            super(itemView);
            image=itemView.findViewById(R.id.consultationMenu_image);
            name=itemView.findViewById(R.id.consultationMenu_name);
            date=itemView.findViewById(R.id.consultationMenu_date);
            time=itemView.findViewById(R.id.consultationMenu_time);
            status=itemView.findViewById(R.id.consultationMenu_status);
            call=itemView.findViewById(R.id.consultationMenu_call);
        }
    }
}
