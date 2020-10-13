package com.stackbuffers.doctor_patient_project.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.stackbuffers.doctor_patient_project.HelperClasses.Session;
import com.stackbuffers.doctor_patient_project.HelperClasses.UserSessionManager;
import com.stackbuffers.doctor_patient_project.Models.Messages;
import com.stackbuffers.doctor_patient_project.R;

import java.util.List;


public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessageViewHolder> {

    private List<Messages> userMessagesList;
    UserSessionManager userSessionManager;
    Session session;

    public MessagesAdapter(List<Messages> userMessagesList, Context context) {
        this.userMessagesList = userMessagesList;
        userSessionManager=new UserSessionManager(context);
        session=userSessionManager.getSessionDetails();
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {
        public TextView senderMessageText, receivermessageText;

        public MessageViewHolder(View itemView) {
            super(itemView);
            senderMessageText = itemView.findViewById(R.id.sender_message_text);
            receivermessageText = itemView.findViewById(R.id.receiver_message_text);

        }

    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_layout_of_users, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MessageViewHolder holder, int position) {
        String messageSenderId = session.getId();
        Messages messages = userMessagesList.get(position);
        String fromUserId = messages.getFrom();
        String fromMessageType = messages.getType();
        if (fromMessageType.equals("text")) {
            holder.receivermessageText.setVisibility(View.INVISIBLE);
            if (fromUserId.equals(messageSenderId)) {
                holder.senderMessageText.setBackgroundResource(R.drawable.my_message);
                holder.senderMessageText.setTextColor(Color.WHITE);
                holder.senderMessageText.setGravity(Gravity.LEFT);
                holder.senderMessageText.setText(messages.getMessage());
            } else {
                holder.senderMessageText.setVisibility(View.INVISIBLE);
                holder.receivermessageText.setVisibility(View.VISIBLE);
                holder.receivermessageText.setBackgroundResource(R.drawable.their_message);
                holder.receivermessageText.setTextColor(Color.WHITE);
                holder.receivermessageText.setGravity(Gravity.LEFT);
                holder.receivermessageText.setText(messages.getMessage());


            }
        }

    }

    @Override
    public int getItemCount() {
        return userMessagesList.size();
    }
}
