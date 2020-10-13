package com.stackbuffers.doctor_patient_project.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.stackbuffers.doctor_patient_project.Activities.Patient.BookDoctorActivity;
import com.stackbuffers.doctor_patient_project.Activities.Patient.ConsultDoctorActivity;
import com.stackbuffers.doctor_patient_project.HelperClasses.SharedHelper;
import com.stackbuffers.doctor_patient_project.Models.SearchDoctorConsultantModel;
import com.stackbuffers.doctor_patient_project.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SearchConsultantAdapter extends RecyclerView.Adapter<SearchConsultantAdapter.modelViewHolder> implements Filterable {
    Context context;
    public List<SearchDoctorConsultantModel> list;
    public List<SearchDoctorConsultantModel> filterList;
    FilterClass filter;

    public SearchConsultantAdapter(Context context, List<SearchDoctorConsultantModel> list) {
        this.context = context;
        this.list = list;
        this.filterList=list;
    }

    @NonNull
    @Override
    public modelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.search_doctor_item_layout, parent, false);
        return new modelViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull modelViewHolder holder, final int i) {
        holder.name.setText(list.get(i).getName());
        holder.specialization.setText("Dr. "+list.get(i).getSpecialization());
        Picasso.get().load(list.get(i).getImage()).into(holder.image);
        holder.bookdoctor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedHelper sharedHelper = new SharedHelper();
                sharedHelper.putKey(context, "cid", list.get(i).getId());
                SendUserToDoctorDetailsActivity();
            }
        });
    }

    private void SendUserToDoctorDetailsActivity() {
        Intent intent = new Intent(context.getApplicationContext(), ConsultDoctorActivity.class);
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
            filter=new FilterClass(this,filterList);
        }
        return filter;
    }

    public class modelViewHolder extends RecyclerView.ViewHolder {
        TextView name, specialization;
        CircleImageView image;
        TextView bookdoctor;

        public modelViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.searchdoctor_name);
            specialization = itemView.findViewById(R.id.searchdoctor_special);
            image = itemView.findViewById(R.id.searchdoctor_image);
            bookdoctor = itemView.findViewById(R.id.searchdoctor_booknow);

        }
    }
}
