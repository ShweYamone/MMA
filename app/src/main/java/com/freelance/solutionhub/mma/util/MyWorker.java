package com.freelance.solutionhub.mma.util;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.freelance.solutionhub.mma.R;
import com.freelance.solutionhub.mma.activity.MainActivity;

import org.phoenixframework.channels.Channel;
import org.phoenixframework.channels.Envelope;
import org.phoenixframework.channels.IMessageCallback;
import org.phoenixframework.channels.Socket;

public class MyWorker extends Worker {

    //a public static string that will be used as the key
    //for sending and receiving data
    public static final String TASK_DESC = "task_desc";

    public MyWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);

        Uri.Builder url = Uri.parse( "ws://hub-nightly-public-alb-1826126491.ap-southeast-1.elb.amazonaws.com/socket/websocket" ).buildUpon();
        // url.appendQueryParameter("vsn", "2.0.0");
        url.appendQueryParameter( "token", "mSharedPreferences.getToken()");
        try {
            Socket socket; Channel channel;

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
            channel.on("mso_created", new IMessageCallback() {
                @Override
                public void onMessage(Envelope envelope) {
                    Log.i("NEW_MESSAGE",envelope.toString());
                    final JsonNode user = envelope.getPayload().get("mso_id");
                    if (user == null || user instanceof NullNode) {
                        ((MainActivity)getApplicationContext()).onMessageNoti("An anonymous user entered","");
                    }
                    else {
                        ((MainActivity)getApplicationContext()).onMessageNoti(envelope.getPayload().get("mso_id")+"","You received an MSO alert!");
                    }

                }
            });

            channel.on("mso_rejected", new IMessageCallback() {
                @Override
                public void onMessage(Envelope envelope) {
                    //  Toast.makeText(getApplicationContext(), "CLOSED: " + envelope.toString(), Toast.LENGTH_SHORT).show();
                    //   tvResult.setText("CLOSED: " + envelope.toString());
                    Log.i("CLOSED", envelope.toString());
                    final JsonNode user = envelope.getPayload().get("mso_id");
                    if (user == null || user instanceof NullNode) {
                        ((MainActivity)getApplicationContext()).onMessageNoti( "An anonymous user entered","");
                    }
                    else {
                        ((MainActivity)getApplicationContext()).onMessageNoti( envelope.getPayload().get("mso_id")+"","You received an MSO alert!");
                    }
                }
            });

            channel.on("announcement", new IMessageCallback() {
                @Override
                public void onMessage(Envelope envelope) {
                    //  Toast.makeText(getApplicationContext(), "CLOSED: " + envelope.toString(), Toast.LENGTH_SHORT).show();
                    //   tvResult.setText("CLOSED: " + envelope.toString());
                    Log.i("CLOSED", envelope.toString());
                    final JsonNode user = envelope.getPayload().get("text");
                    if (user == null || user instanceof NullNode) {
                        ((MainActivity)getApplicationContext()).onMessageNoti("An anonymous user entered","");
                    }
                    else {
                        ((MainActivity)getApplicationContext()).onMessageNoti( "Announcement",""+envelope.getPayload().get("text"));
                    }
                }
            });

        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Exception" + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.i("Websocket",  e.getMessage() + "\n" + e.getLocalizedMessage());
        }
    }



    @NonNull
    @Override
    public Result doWork() {

        //getting the input data
        String taskDesc = getInputData().getString(TASK_DESC);

        displayNotification("My Worker", taskDesc);
        return Result.success();
    }


    private void displayNotification(String title, String task) {
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("simplifiedcoding", "simplifiedcoding", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder notification = new NotificationCompat.Builder(getApplicationContext(), "simplifiedcoding")
                .setContentTitle(title)
                .setContentText(task)
                .setSmallIcon(R.mipmap.ic_launcher);

        notificationManager.notify(2, notification.build());
    }
}
