package com.stackbuffers.doctor_patient_project.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.stackbuffers.doctor_patient_project.Adapters.MessagesAdapter;
import com.stackbuffers.doctor_patient_project.HelperClasses.PostRequestData;
import com.stackbuffers.doctor_patient_project.HelperClasses.Session;
import com.stackbuffers.doctor_patient_project.HelperClasses.URLHelper;
import com.stackbuffers.doctor_patient_project.HelperClasses.UserSessionManager;
import com.stackbuffers.doctor_patient_project.Models.MessageData;
import com.stackbuffers.doctor_patient_project.Models.Messages;
import com.stackbuffers.doctor_patient_project.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChatActivity extends AppCompatActivity {
    private ImageView sendmessagebutton;
    private EditText userMessageInput;
    private TextView receiverName;
    private CircleImageView recieverProfileImge;
    private String messageReceiverId, messageReceiverName, messageSenderId, messageReceiverImage;
    private RecyclerView usermessageslist;
    private DatabaseReference rootRef, msgsref;
    private FirebaseAuth mAuth;
    private String saveCurrentDate, saveCurrentTime;
    private final List<Messages> messagesList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private MessagesAdapter messagesAdapter;
    UserSessionManager userSessionManager;
    Session session;
    ImageView back;
    String token = "";
    String request_type = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        request_type = getIntent().getStringExtra("user_type");
        intializedFields();
        changeStatusBarColor();
        mAuth = FirebaseAuth.getInstance();
        messageSenderId = session.getId();
        rootRef = FirebaseDatabase.getInstance().getReference();
        messageReceiverId = getIntent().getStringExtra("receiverid");
        token = getIntent().getStringExtra("token");
        messageReceiverName = getIntent().getStringExtra("receivername");
        messageReceiverImage = getIntent().getStringExtra("receiverImage");
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        dispalyReceiverInfo();
        sendmessagebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
                closeKeyboard();
            }
        });
        fetchMessages();
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            window.setStatusBarColor(Color.TRANSPARENT);
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }
    }

    private void closeKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void fetchMessages() {
        messagesList.clear();
        rootRef.child("Messages").child(messageSenderId).child(messageReceiverId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists()) {
                    Messages messages = dataSnapshot.getValue(Messages.class);
                    messagesList.add(messages);
                    messagesAdapter = new MessagesAdapter(messagesList, ChatActivity.this);
                    messagesAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                        @Override
                        public void onChanged() {
                            usermessageslist.scrollToPosition(messagesAdapter.getItemCount() - 1);
                        }
                    });
                    usermessageslist.setAdapter(messagesAdapter);
                    messagesAdapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void sendMessage() {
        Calendar calFordDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        saveCurrentDate = currentDate.format(calFordDate.getTime());

        Calendar calFordTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm aa");
        saveCurrentTime = currentTime.format(calFordTime.getTime());
        String messageText = userMessageInput.getText().toString();
        if (request_type.equals("user")) {
            newmessasegsend(messageText);
        } else {
            sendNotification(messageText);
        }
        if (TextUtils.isEmpty(messageText)) {
            Toast.makeText(getApplicationContext(), "Please Write Message", Toast.LENGTH_SHORT).show();
        } else {
            String message_sender_ref = "Messages/" + messageSenderId + "/" + messageReceiverId;
            String message_receiver_ref = "Messages/" + messageReceiverId + "/" + messageSenderId;

            DatabaseReference user_message_key = rootRef.child("Messages").child(messageSenderId).child(messageReceiverId).push();
            String message_push_id = user_message_key.getKey();


            Map messageTextBody = new HashMap();
            messageTextBody.put("message", messageText);
            messageTextBody.put("time", saveCurrentTime);
            messageTextBody.put("date", saveCurrentDate);
            messageTextBody.put("type", "text");
            messageTextBody.put("from", messageSenderId);

            Map messageBodyDetails = new HashMap();
            messageBodyDetails.put(message_sender_ref + "/" + message_push_id, messageTextBody);
            messageBodyDetails.put(message_receiver_ref + "/" + message_push_id, messageTextBody);
            msgsref = rootRef.child("MessagesUsers").child(session.getId()).child(messageReceiverId);
            msgsref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.exists()) {
                        HashMap map = new HashMap();
                        map.put("name", session.getName());
                        map.put("image", session.getImage());
                        map.put("id", session.getId());

                        msgsref.updateChildren(map).addOnCompleteListener(new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task) {
                                if (task.isSuccessful()) {

                                }
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            rootRef.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {
                      //  fetchMessages();
                        userMessageInput.setText("");
                    } else {
                        String message = task.getException().getMessage();
                        Toast.makeText(getApplicationContext(), "Message sent Fail :" + message, Toast.LENGTH_SHORT).show();
                        userMessageInput.setText("");

                    }


                }
            });

        }
    }

    private void sendNotification(String message) {
        Gson gson = new Gson();
        MessageData data = new MessageData();
        data.setTitle("New  Message From " + session.getName());
        data.setMessage(message);
        PostRequestData postRequestData = new PostRequestData();
        postRequestData.setTo(token);
        postRequestData.setData(data);
        String json = gson.toJson(postRequestData);
        String url = "https://fcm.googleapis.com/fcm/send";

        final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        OkHttpClient client = new OkHttpClient();


        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .header("Authorization", "key=AAAAuPX4i2E:APA91bHO1ftlGdOCbpKbv2JtCn8up8HWSb9bpTjxUdIb7iqE-IHZh9pbzjSDgd8NT5qUULu3fxdgMKKSmIv2-4tVkKBC5rDKPYELa6MrCJi9XVHFZg0jUXTdPLAVLSr-WT74rAdM9bP6")
                .post(body)
                .build();


        Callback responseCallBack = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.v("Fail Message", e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.v("response", response.toString());
            }


        };
        okhttp3.Call call = client.newCall(request);
        call.enqueue(responseCallBack);
    }

    public void newmessasegsend(String message) {
        Gson gson = new Gson();
        MessageData data = new MessageData();
        data.setTitle("New  Message From " + session.getName());
        data.setMessage(message);
        PostRequestData postRequestData = new PostRequestData();
        postRequestData.setTo(token);
        postRequestData.setData(data);
        String json = gson.toJson(postRequestData);
        String url = "https://fcm.googleapis.com/fcm/send";

        final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        OkHttpClient client = new OkHttpClient();


        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .header("Authorization", "key=AAAAuPX4i2E:APA91bHO1ftlGdOCbpKbv2JtCn8up8HWSb9bpTjxUdIb7iqE-IHZh9pbzjSDgd8NT5qUULu3fxdgMKKSmIv2-4tVkKBC5rDKPYELa6MrCJi9XVHFZg0jUXTdPLAVLSr-WT74rAdM9bP6")
                .post(body)
                .build();


        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("user_id", messageSenderId);
            jsonObject.put("doc_id", messageReceiverId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody bodyMessage = RequestBody.create(jsonObject.toString(), JSON);
        Log.d("OkHTTP", "Request Created");
        Request requestMessage = new Request.Builder()
                .url(URLHelper.SET_MESSAGES)
                .post(bodyMessage)
                .build();
        Callback responseCallBack = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.v("Fail Message", e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.v("response", response.toString());
            }


        };

        Callback responseCallBackDB = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.v("Fail Message", e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.v("response Send", response.toString());
            }


        };
        okhttp3.Call call = client.newCall(request);
        call.enqueue(responseCallBack);
        okhttp3.Call calltwo = client.newCall(requestMessage);
        calltwo.enqueue(responseCallBackDB);

    }

    private void dispalyReceiverInfo() {
        receiverName.setText(messageReceiverName);
        Picasso.get().load(messageReceiverImage).placeholder(R.drawable.add_profile).into(recieverProfileImge);
    }

    private void intializedFields() {


        userSessionManager = new UserSessionManager(this);
        session = userSessionManager.getSessionDetails();
        back = findViewById(R.id.backward_messages);
        receiverName = (TextView) findViewById(R.id.custom_profile_namee);
        recieverProfileImge = (CircleImageView) findViewById(R.id.custom_profile_image);
        sendmessagebutton = findViewById(R.id.send_message_button);
        userMessageInput = (EditText) findViewById(R.id.input_message);
        usermessageslist = (RecyclerView) findViewById(R.id.messages_list_users);


        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        usermessageslist.setHasFixedSize(true);
        usermessageslist.setLayoutManager(linearLayoutManager);


    }

}

