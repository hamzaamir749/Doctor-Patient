package com.stackbuffers.doctor_patient_project.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.stackbuffers.doctor_patient_project.Activities.Doctor.TodayBookingsActivity;
import com.stackbuffers.doctor_patient_project.Activities.Doctor.TodayConsultationsActivity;
import com.stackbuffers.doctor_patient_project.Activities.Patient.HospitalsActivity;
import com.stackbuffers.doctor_patient_project.Activities.Patient.SearchConsultantActivity;
import com.stackbuffers.doctor_patient_project.Activities.Patient.SearchDoctorsActivity;
import com.stackbuffers.doctor_patient_project.Models.HomeModel;
import com.stackbuffers.doctor_patient_project.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.modelViewHolder> {

    List<HomeModel> list;
    Context context;

    public HomeAdapter(List<HomeModel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public modelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.home_content_layout, parent, false);
        return new modelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull modelViewHolder holder, final int position) {
        holder.txtName.setText(list.get(position).getName());
        holder.txtTotal.setText(list.get(position).getTotal());
        Picasso.get().load(list.get(position).getIcons()).into(holder.icon);

        holder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (list.get(position).getName().equals(context.getResources().getString(R.string.FindDoctors))) {
                    context.startActivity(new Intent(context.getApplicationContext(), SearchDoctorsActivity.class));
                } else if (list.get(position).getName().equals(context.getResources().getString(R.string.FindConsultant))) {
                    context.startActivity(new Intent(context.getApplicationContext(), SearchConsultantActivity.class));
                } else if (list.get(position).getName().equals(context.getResources().getString(R.string.FindHospital))) {
                    context.startActivity(new Intent(context.getApplicationContext(), HospitalsActivity.class));
                }
                if (list.get(position).getName().equals(context.getResources().getString(R.string.TodayAppointments))) {
                    context.startActivity(new Intent(context.getApplicationContext(), TodayBookingsActivity.class));
                } else if (list.get(position).getName().equals(context.getResources().getString(R.string.TodayConsultations))) {
                    context.startActivity(new Intent(context.getApplicationContext(), TodayConsultationsActivity.class));
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class modelViewHolder extends RecyclerView.ViewHolder {
        CircleImageView icon;
        TextView txtName, txtTotal;
        CardView item;

        public modelViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.home_icon);
            txtName = itemView.findViewById(R.id.home_name);
            txtTotal = itemView.findViewById(R.id.home_total);
            item = itemView.findViewById(R.id.home_card_type);
        }
    }
}
