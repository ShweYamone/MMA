package com.digisoft.mma.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.digisoft.mma.R;
import com.digisoft.mma.model.NotificationModel;
import com.digisoft.mma.util.Network;
import com.digisoft.mma.util.SharePreferenceHelper;

import org.phoenixframework.channels.Channel;
import org.phoenixframework.channels.Envelope;
import org.phoenixframework.channels.IMessageCallback;
import org.phoenixframework.channels.Socket;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.digisoft.mma.util.AppConstant.user_inactivity_time;

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
    private Network network;
    private SharePreferenceHelper sharePreferenceHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_view);
        ButterKnife.bind(this);
        network = new Network(this);
        if(!network.isNetworkAvailable())
            finish();
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


        /************WebScoket***************/
        Uri.Builder url = Uri.parse( "ws://hub-nightly-public-alb-1826126491.ap-southeast-1.elb.amazonaws.com/socket/websocket" ).buildUpon();
        // url.appendQueryParameter("vsn", "2.0.0");
        url.appendQueryParameter( "token", sharePreferenceHelper.getToken());
        Log.i("notification_id",notificationModel.getId());
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
                            new LoadImage().execute(new Boolean[]{true});
                        }
                    })
                    .receive("error", new IMessageCallback() {
                        @Override
                        public void onMessage(Envelope envelope) {
                            Log.i("Websocket", "NOT Joined with ");
                        }
                    });
//
//            channel.on("mso_created", new IMessageCallback() {
//                @Override
//                public void onMessage(Envelope envelope) {
//                    Log.i("NEW_MESSAGE",envelope.toString());
//                }
//            });
//
//            channel.on("mso_rejected", new IMessageCallback() {
//                @Override
//                public void onMessage(Envelope envelope) {
//                    //  Toast.makeText(getApplicationContext(), "CLOSED: " + envelope.toString(), Toast.LENGTH_SHORT).show();
//                    //   tvResult.setText("CLOSED: " + envelope.toString());
//                    Log.i("CLOSED", envelope.toString());
//                }
//            });


        } catch (Exception e) {
            //Toast.makeText(getApplicationContext(), "Exception" + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.i("Websocket",  e.getMessage() + "\n" + e.getLocalizedMessage());
        }

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
                sharePreferenceHelper.setLock(false);
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

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        sharePreferenceHelper.setLock(false);
    }

    class LoadImage extends AsyncTask<Boolean, Void, Boolean> {

        public LoadImage() {

        }

        @Override
        protected void onPostExecute(Boolean aVoid) {
            super.onPostExecute(aVoid);
        }


        @Override
        protected Boolean doInBackground(Boolean... b) {
            if(b[0]){

//Sending a message. This library uses Jackson for JSON serialization
                ObjectNode node = new ObjectNode(JsonNodeFactory.instance)
                        .put("notification_id",notificationModel.getId());
                try {
                    channel.push("notification_read",node);
                    Log.i("notification_read",channel.canPush()+"");
                } catch (IOException e) {
                   Log.i("NOTIFICATION_READ_ERROR",e.getMessage());
                }
            }

            return false;
        }

    }
}
