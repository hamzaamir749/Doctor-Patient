package com.stackbuffers.doctor_patient_project.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.stackbuffers.doctor_patient_project.Models.BookingStatusModel;
import com.stackbuffers.doctor_patient_project.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class BookingStatusAdapter extends RecyclerView.Adapter<BookingStatusAdapter.modelViewHolder> {
    Context context;
    List<BookingStatusModel> list;

    public BookingStatusAdapter(Context context, List<BookingStatusModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public modelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view= LayoutInflater.from(context).inflate(R.layout.booking_menu_items_layout,parent,false);
        return new modelViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull modelViewHolder holder, int i) {
        holder.name.setText("DR. "+list.get(i).getName());
        holder.hospital.setText(list.get(i).getHospital());
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

            holder.status.setText(status);
        }

    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public class modelViewHolder extends RecyclerView.ViewHolder {
        CircleImageView image;
        TextView name,hospital,date,time;
        Button status;
        public modelViewHolder(@NonNull View itemView) {
            super(itemView);
            image=itemView.findViewById(R.id.bookingMenu_image);
            name=itemView.findViewById(R.id.bookingMenu_name);
            hospital=itemView.findViewById(R.id.bookingMenu_hospital);
            date=itemView.findViewById(R.id.bookingMenu_date);
            time=itemView.findViewById(R.id.bookingMenu_time);
            status=itemView.findViewById(R.id.bookingMenu_status);
        }
    }
}
