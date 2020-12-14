package com.freelance.solutionhub.mma.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.freelance.solutionhub.mma.R;
import com.freelance.solutionhub.mma.adapter.NotificationAdapter;
import com.freelance.solutionhub.mma.model.NotificationModel;
import com.freelance.solutionhub.mma.util.SharePreferenceHelper;
import com.freelance.solutionhub.mma.util.WebSocketUtils;


import org.phoenixframework.channels.Channel;
import org.phoenixframework.channels.ChannelEvent;
import org.phoenixframework.channels.Envelope;
import org.phoenixframework.channels.IMessageCallback;
import org.phoenixframework.channels.Socket;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NotificationActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recyclerview_NotiList)
    RecyclerView recyclerView;

    private NotificationAdapter mAdapter;
    private List<NotificationModel> notificationList = new ArrayList<>();

    private Socket socket;
    private Channel channel;
    private WebSocketUtils webSocketUtils;

    private SharePreferenceHelper mSharedPreference;
    private String urlStr, channelStr;

    private static final String TAG = "NotificationActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        ButterKnife.bind(this);
        setupToolbar();
        mSharedPreference = new SharePreferenceHelper(this);
        mAdapter = new NotificationAdapter(this, notificationList);
        webSocketUtils = new WebSocketUtils(this);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(mAdapter);
        prepareNotifications();


        /************WebScoket***************/
        Uri.Builder url = Uri.parse( "ws://hub-nightly-public-alb-1826126491.ap-southeast-1.elb.amazonaws.com/socket/websocket" ).buildUpon();
       // url.appendQueryParameter("vsn", "2.0.0");
        url.appendQueryParameter( "token", mSharedPreference.getToken());
        try {
            //    Log.i("Websocket", url.toString());
            socket = new Socket(url.build().toString());
            socket.connect();
            if(socket.isConnected()){
                Log.i("SOCKET_CONNECT","SUCCESS");
            }

            channel = socket.chan("notification", null);

            channel.join()
                    .receive("ok", new IMessageCallback() {
                        @Override
                        public void onMessage(Envelope envelope) {
                            Log.i("OK", "Joined with " + envelope.toString());
                        }
                    })
                    .receive("error", new IMessageCallback() {
                        @Override
                        public void onMessage(Envelope envelope) {
                            Log.i("Websocket", "NOT Joined with ");
                        }
                    });
            channel.on("mso_created", new IMessageCallback() {
                @Override
                public void onMessage(Envelope envelope) {
                    //Toast.makeText(getApplicationContext(), "NEW MESSAGE: " + envelope.toString(), Toast.LENGTH_SHORT).show();
                    //tvResult.setText("NEW MESSAGE: " + envelope.toString());
                    Log.i("NEW_MESSAGE",envelope.toString());
                }
            });

            channel.on("mso_rejected", new IMessageCallback() {
                @Override
                public void onMessage(Envelope envelope) {
                  //  Toast.makeText(getApplicationContext(), "CLOSED: " + envelope.toString(), Toast.LENGTH_SHORT).show();
                     //   tvResult.setText("CLOSED: " + envelope.toString());
                    Log.i("CLOSED", envelope.toString());
                }
            });


//Sending a message. This library uses Jackson for JSON serialization
//            ObjectNode node = new ObjectNode(JsonNodeFactory.instance)
//                    .put("user", "my_username")
//                    .put("body", "Hello");
//
//            channel.push("new:msg", node);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Exception" + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.i("Websocket",  e.getMessage() + "\n" + e.getLocalizedMessage());
        }

     //   Toast.makeText(getApplicationContext(), "END", Toast.LENGTH_SHORT).show();
        Log.i("END",  "END");


}

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        final ActionBar ab = getSupportActionBar();
        //ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);
    }

    private void prepareNotifications() {
        NotificationModel obj = new NotificationModel("Announcement", "This is message. This is message. This is message. This is message. Fighting", "12/5/2020/ 05:54PM");
        notificationList.add(obj); notificationList.add(obj); notificationList.add(obj);
        notificationList.add(obj); notificationList.add(obj); notificationList.add(obj);
        notificationList.add(obj); notificationList.add(obj); notificationList.add(obj);

    }

    //For Back Arrow
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }


        return super.onOptionsItemSelected(item);
    }
}
