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

import org.phoenixframework.channels.*;

import java.io.IOException;
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

    private Socket socket;
    private Channel channel;
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

        Uri.Builder url = Uri.parse( "ws://hub-nightly-public-alb-1826126491.ap-southeast-1.elb.amazonaws.com/socket/websocket" ).buildUpon();
        //   url.appendQueryParameter("vsn", "2.0.0");
        url.appendQueryParameter( "token", mSharedPreference.getToken());
        try {
            //    Log.i("Websocket", url.toString());
            socket = new Socket(url.build().toString());
            socket.connect();

            channel = socket.chan("notification", null);

            channel.join()
                    .receive("ok", new IMessageCallback() {
                        @Override
                        public void onMessage(Envelope envelope) {
                            Log.i("Websocket", "Joined with " + envelope.toString());
                        }
                    })
                    .receive("error", new IMessageCallback() {
                        @Override
                        public void onMessage(Envelope envelope) {
                            Log.i("Websocket", "NOT Joined with ");
                        }
                    })
            ;
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Exception" + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.i("Websocket",  e.getMessage() + "\n" + e.getLocalizedMessage());
        }
        Toast.makeText(getApplicationContext(), "END", Toast.LENGTH_SHORT).show();
        Log.i("Websocket",  "END");

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
