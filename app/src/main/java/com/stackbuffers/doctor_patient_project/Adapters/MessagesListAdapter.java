package com.stackbuffers.doctor_patient_project.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.stackbuffers.doctor_patient_project.Activities.ChatActivity;
import com.stackbuffers.doctor_patient_project.Models.MessagesModel;
import com.stackbuffers.doctor_patient_project.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessagesListAdapter  extends RecyclerView.Adapter<MessagesListAdapter.modelViewHolder> {
    List<MessagesModel> list;
    Context context;

    public MessagesListAdapter(List<MessagesModel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public modelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_messages_layout, parent, false);
        return new modelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull modelViewHolder holder, final int position) {
        Picasso.get().load(list.get(position).getImage()).into(holder.image);
        holder.name.setText(list.get(position).getName());
        holder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context.getApplicationContext(), ChatActivity.class);
                intent.putExtra("user_type",list.get(position).getType());
                intent.putExtra("receiverid",list.get(position).getId());
                intent.putExtra("receivername",list.get(position).getName());
                intent.putExtra("receiverImage",list.get(position).getImage());
                intent.putExtra("token",list.get(position).getToken());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class modelViewHolder extends RecyclerView.ViewHolder{
        CircleImageView image;
        TextView name;
        LinearLayout item;
        public modelViewHolder(@NonNull View itemView) {
            super(itemView);
            image=itemView.findViewById(R.id.ml_image);
            name=itemView.findViewById(R.id.ml_name);
            item=itemView.findViewById(R.id.ml_item_list);
        }
    }
}
