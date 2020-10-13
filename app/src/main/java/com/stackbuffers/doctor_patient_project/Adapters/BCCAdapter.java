package com.stackbuffers.doctor_patient_project.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.stackbuffers.doctor_patient_project.Models.BccModel;
import com.stackbuffers.doctor_patient_project.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class BCCAdapter extends RecyclerView.Adapter<BCCAdapter.modelViewHolder> {

    List<BccModel> list;
    Context context;

    public BCCAdapter(List<BccModel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public modelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.bookingconsultations_complete_layout, parent, false);
        return new modelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull modelViewHolder holder, int position) {
        holder.name.setText(list.get(position).getName());
        holder.time.setText(list.get(position).getTime());
        holder.date.setText(list.get(position).getDate());
        Picasso.get().load(list.get(position).getImage()).into(holder.imageView);
        if (list.get(position).getHospitalName().equals("")) {
            holder.hospitalName.setVisibility(View.GONE);
        } else {
            holder.hospitalName.setText(list.get(position).getHospitalName());

        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class modelViewHolder extends RecyclerView.ViewHolder {
        TextView name, date, time,hospitalName;
        CircleImageView imageView;

        public modelViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.bcc_name);
            date = itemView.findViewById(R.id.bcc_date);
            time = itemView.findViewById(R.id.bcc_time);
            imageView = itemView.findViewById(R.id.bcc_image);
            hospitalName = itemView.findViewById(R.id.bcc_hospital);
        }
    }
}
