package com.stackbuffers.doctor_patient_project.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.stackbuffers.doctor_patient_project.Activities.Patient.HospitalDetailsActivity;
import com.stackbuffers.doctor_patient_project.HelperClasses.SharedHelper;
import com.stackbuffers.doctor_patient_project.HelperClasses.URLHelper;
import com.stackbuffers.doctor_patient_project.Models.HospitalModel;
import com.stackbuffers.doctor_patient_project.Models.MainHospitalModel;
import com.stackbuffers.doctor_patient_project.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeHospitalsAdapter extends RecyclerView.Adapter<HomeHospitalsAdapter.modelViewHolder> {

    List<MainHospitalModel> list;
    Context context;

    public HomeHospitalsAdapter(List<MainHospitalModel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public modelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.main_hospital_items, parent, false);
        return new modelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull modelViewHolder holder, final int position) {
        Picasso.get().load(list.get(position).getImage()).into(holder.imageView);
        holder.name.setText(list.get(position).getName());
        holder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedHelper sharedHelper=new SharedHelper();
                sharedHelper.putKey(context,"hid",String.valueOf(list.get(position).getId()));
                Intent intent=new Intent(context.getApplicationContext(), HospitalDetailsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class modelViewHolder extends RecyclerView.ViewHolder {
        CardView item;
        CircleImageView imageView;
        TextView name;
        public modelViewHolder(@NonNull View itemView) {
            super(itemView);

            item=itemView.findViewById(R.id.main_hospital_card);
            imageView=itemView.findViewById(R.id.main_hospital_image);
            name=itemView.findViewById(R.id.main_hospital_name);
        }
    }
}
