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
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;
import com.stackbuffers.doctor_patient_project.Activities.Same.VideoChatViewActivity;
import com.stackbuffers.doctor_patient_project.HelperClasses.Session;
import com.stackbuffers.doctor_patient_project.HelperClasses.URLHelper;
import com.stackbuffers.doctor_patient_project.HelperClasses.UserSessionManager;
import com.stackbuffers.doctor_patient_project.Models.EPModel;
import com.stackbuffers.doctor_patient_project.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import libs.mjn.prettydialog.PrettyDialog;
import libs.mjn.prettydialog.PrettyDialogCallback;

public class EPAdapter extends RecyclerView.Adapter<EPAdapter.modelViewHolder>{
    List<EPModel> list;
    Context context;
    UserSessionManager userSessionManager;
    Session session;
    String token,channel;

    public EPAdapter(List<EPModel> list, Context context) {
        this.list = list;
        this.context = context;
        userSessionManager=new UserSessionManager(context);
        session=userSessionManager.getSessionDetails();
    }

    @NonNull
    @Override
    public modelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_emergency_patient_layout, parent, false);
        return new modelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull modelViewHolder holder,final int position) {
        Picasso.get().load(list.get(position).getImage()).into(holder.imageView);
        holder.name.setText(list.get(position).getName());
        channel=list.get(position).getChannel();
        token=list.get(position).getToken();
        holder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AcceptRequest(list.get(position).getId());

            }
        });
    }

    private void AcceptRequest(final String id) {
        final ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Please Wait");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLHelper.APPROVE_EMERGECY_PATIENT, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                try {
                    JSONObject object = new JSONObject(response);
                    boolean status = object.getBoolean("status");
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
                                                Intent intent=new Intent(context, VideoChatViewActivity.class);
                                                intent.putExtra("tokenn",token);
                                                intent.putExtra("channell",channel);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                context.startActivity(intent);

                                            }
                                        }
                                )
                                .show();
                    }
                    else {
                        Toast.makeText(context, "Try Again", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show();
                AcceptRequest(id);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("emergency_id", id);

                return map;
            }
        };
        Volley.newRequestQueue(context).add(stringRequest);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class modelViewHolder extends RecyclerView.ViewHolder {
        Button item;
        CircleImageView imageView;
        TextView name;

        public modelViewHolder(@NonNull View itemView) {
            super(itemView);

            item = itemView.findViewById(R.id.ep_approve);
            imageView = itemView.findViewById(R.id.ep_image);
            name = itemView.findViewById(R.id.ep_name);
        }
    }
}
