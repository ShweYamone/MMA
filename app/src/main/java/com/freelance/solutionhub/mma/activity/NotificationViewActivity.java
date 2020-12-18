package com.freelance.solutionhub.mma.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.freelance.solutionhub.mma.R;
import com.freelance.solutionhub.mma.model.NotificationModel;
import com.freelance.solutionhub.mma.util.SharePreferenceHelper;

import org.phoenixframework.channels.Channel;
import org.phoenixframework.channels.Envelope;
import org.phoenixframework.channels.IMessageCallback;
import org.phoenixframework.channels.Socket;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.freelance.solutionhub.mma.util.AppConstant.user_inactivity_time;

public class NotificationViewActivity extends AppCompatActivity {

    @BindView(R.id.tvMessageHead)
    TextView tvMessageHead;

    @BindView(R.id.tvMessageBody)
    TextView tvMessageBody;

    @BindView(R.id.tvDateTime)
    TextView tvDataTime;

    private Handler handler;
    private Runnable r;
    private Socket socket;
    private Channel channel;
    private NotificationModel notificationModel;
    private boolean startHandler = true;
    private SharePreferenceHelper sharePreferenceHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_view);
        ButterKnife.bind(this);
        sharePreferenceHelper = new SharePreferenceHelper(this);
        sharePreferenceHelper.setLock(false);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        notificationModel = (NotificationModel)getIntent().getSerializableExtra("noti");
        if(notificationModel == null)
            finish();
        tvMessageHead.setText(notificationModel.getMessageHead());
        tvMessageBody.setText(notificationModel.getMessageBody());
        tvDataTime.setText(notificationModel.getMessageDateTime());


        /**
         after certain amount of user inactivity, asks for passcode
         */
        handler = new Handler();
        r = new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                Toast.makeText(NotificationViewActivity.this, "user is inactive from last 5 minutes",Toast.LENGTH_SHORT).show();
                startHandler = false;
                Intent intent = new Intent(NotificationViewActivity.this, PasscodeActivity.class);
                intent.putExtra("workInMiddle", "work");
                startActivity(intent);
                stopHandler();
            }
        };

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

    @Override
    protected void onStop() {
        super.onStop();
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        boolean isScreenOn = pm.isInteractive();
        if (isScreenOn)
            stopHandler();
        else
            sharePreferenceHelper.setLock(true);
    }

    @Override
    public void onUserInteraction() {
        // TODO Auto-generated method stub
        super.onUserInteraction();
      //  Toast.makeText(this, "UserInteraction", Toast.LENGTH_SHORT).show();
        stopHandler();//stop first and then start
        if (startHandler)
            startHandler();
    }
    public void stopHandler() {
        handler.removeCallbacks(r);
    }
    public void startHandler() {
        handler.postDelayed(r, user_inactivity_time); //for 3 minutes
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sharePreferenceHelper.getLock()) {
            Intent intent = new Intent(NotificationViewActivity.this, PasscodeActivity.class);
            intent.putExtra("workInMiddle", "work");
            startActivity(intent);
        } else {
            startHandler = true;
            startHandler();
        }

        /************WebScoket***************/
        Uri.Builder url = Uri.parse( "ws://hub-nightly-public-alb-1826126491.ap-southeast-1.elb.amazonaws.com/socket/websocket" ).buildUpon();
        // url.appendQueryParameter("vsn", "2.0.0");
        url.appendQueryParameter( "token", sharePreferenceHelper.getToken());
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
                            Log.i("JOINED_WITH", "Joined with " + envelope.toString());
                            Log.i("ON_MESSAGE", "onMessage: " + socket.isConnected());
                        }
                    })
                    .receive("error", new IMessageCallback() {
                        @Override
                        public void onMessage(Envelope envelope) {
                            Log.i("Websocket", "NOT Joined with ");
                        }
                    });

//Sending a message. This library uses Jackson for JSON serialization
            ObjectNode node = new ObjectNode(JsonNodeFactory.instance)
                    .put("notification_id",notificationModel.getId());

            channel.push("notification_read", node);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Exception" + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.i("Websocket",  e.getMessage() + "\n" + e.getLocalizedMessage());
        }
    }

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        sharePreferenceHelper.setLock(true);
    }
}
