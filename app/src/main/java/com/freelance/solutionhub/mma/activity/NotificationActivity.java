package com.freelance.solutionhub.mma.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.freelance.solutionhub.mma.R;
import com.freelance.solutionhub.mma.adapter.NotificationAdapter;
import com.freelance.solutionhub.mma.model.NotificationModel;
import com.freelance.solutionhub.mma.util.SharePreferenceHelper;
import com.freelance.solutionhub.mma.util.WebSocketUtils;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.internal.Utils;

public class NotificationActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recyclerview_NotiList)
    RecyclerView recyclerView;

    private NotificationAdapter mAdapter;
    private List<NotificationModel> notificationList = new ArrayList<>();
    /*
    private Socket socket;
    private Channel channel;*/
    private SharePreferenceHelper mSharedPreference;

    private static final String TAG = "NotificationActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        ButterKnife.bind(this);
        setupToolbar();
        mSharedPreference = new SharePreferenceHelper(this);
        mAdapter = new NotificationAdapter(this, notificationList);

        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(mAdapter);
        prepareNotifications();



        /************WebScoket***************/
/*
        final WebSocketUtils utils = new WebSocketUtils(getApplicationContext());
        final String url = utils.getUrl();
        final String topic = utils.getTopic();
        try {
            socket = new Socket(
                    "hub-nightly-public-alb-1826126491.ap-southeast-1.elb.amazonaws.com/socket/websocket?vsn=2.0.0&token="
                                + mSharedPreference.getToken()
            );
            socket.connect();

            socket.onOpen(new ISocketOpenCallback() {
                @Override
                public void onOpen() {
                    Toast.makeText(getApplicationContext(), "connected" ,Toast.LENGTH_SHORT).show();
                    channel = socket.chan(topic, null);
                    try {
                        channel.join().receive("ok", new IMessageCallback() {
                            @Override
                            public void onMessage(Envelope envelope) {
                                Log.i("WEBSOCKET", "onMessage: " + "Joined Notification Successfully");
                            }
                        });
                    } catch (Exception e) {
                        Log.e("WEBSOCKET", "Failed to join channel " + topic, e);
                     //   handleTerminalError(e);
                    }

                }
            });

        } catch (Exception e) {
            Log.e(TAG, "Failed to connect", e);
          //  handleTerminalError(e);
        }
*/
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
}
