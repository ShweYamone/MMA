package com.digisoft.mma.util;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.digisoft.mma.R;

import org.phoenixframework.channels.Channel;
import org.phoenixframework.channels.Envelope;
import org.phoenixframework.channels.IMessageCallback;
import org.phoenixframework.channels.Socket;

public class ForegroundService extends Service {
    public static final String CHANNEL_ID = "ForegroundServiceChannel";
    private Socket socket;
    private Channel channel;
    Uri.Builder url;


    private BroadcastReceiver networkChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("NetworkConnectivity","Network connectivity change");
//            Log.d("NetworkConnectivity", "onReceive: Socket Disconnet" + socket.isConnected());
            Network network = new Network(context);
            if (network.isNetworkAvailable()) {
                Log.d("NetworkConnectivity", "onReceive: Connection Reconnects");
                if (socket != null && !socket.isConnected()) {
                    createSocketConnection();
                }

            } else {
                Log.d("NetworkConnectivity", "onReceive: Connection Lost");
            }
        }
    };

    private void createSocketConnection() {
        SharePreferenceHelper sharePreferenceHelper = new SharePreferenceHelper(this);

        url = Uri.parse( "ws://hub-nightly-public-alb-1826126491.ap-southeast-1.elb.amazonaws.com/socket/websocket" ).buildUpon();
        // url.appendQueryParameter("vsn", "2.0.0");
        url.appendQueryParameter( "token", sharePreferenceHelper.getToken());
        try {
            socket = new Socket(url.build().toString(), 2000);
            socket.connect();

            //   socket
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
                        onMessageNoti("An anonymous user entered","");
                    }
                    else {
                        onMessageNoti(envelope.getPayload().get("mso_id")+"","You received an MSO alert!");
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
                        onMessageNoti("An anonymous user entered","");
                    }
                    else {
                        onMessageNoti(envelope.getPayload().get("mso_id")+"","You received an MSO alert!");
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
                        onMessageNoti("An anonymous user entered","");
                    }
                    else {
                        onMessageNoti("Announcement",""+envelope.getPayload().get("text"));
                    }
                }
            });

        }
        catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Exception" + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.i("Websocket",  e.getMessage() + "\n" + e.getLocalizedMessage());
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        createSocketConnection();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
//        intentFilter.addAction(ConnectivityManager.C);
        registerReceiver(networkChangeReceiver, intentFilter);
    }

    public void onMessageNoti(String envolope,String mso_type){
        Runnable r = (new Runnable() {
            @Override
            public void run() {
                sendNotification(envolope,mso_type);
            }
        });
        r.run();
    }
    //  @OnClick(R.id.button)
    public void sendNotification(String title, String type) {

        if (type.length() > 95) {
            type = type.substring(0, 90) + " ... ";
        }

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String NOTIFICATION_CHANNEL_ID = "tutorialspoint_01";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            @SuppressLint("WrongConstant") NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "My Notifications", NotificationManager.IMPORTANCE_MAX);
            // Configure the notification channel.
            notificationChannel.setDescription("Sample Channel description");
            notificationChannel.enableLights(true);
            notificationChannel.setShowBadge(true);
            notificationChannel.setLightColor(Color.BLUE);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.enableVibration(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }


        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        notificationBuilder
                .setDefaults(Notification.BADGE_ICON_SMALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.mma_notification)
                .setContentTitle(title)
                .setContentText(type)
                .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
                .setAutoCancel(true)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(type));
        notificationManager.notify(0, notificationBuilder.build());
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String input = intent.getStringExtra("inputExtra");
        createNotificationChannel();
        // Intent notificationIntent = new Intent(this, MainActivity.class);
        // PendingIntent pendingIntent = PendingIntent.getActivity(this,
        //       0, notificationIntent, 0);
        NotificationCompat.Builder notification = new NotificationCompat.Builder(this, CHANNEL_ID);
        notification
                .setContentTitle("Background")
                .setContentText(input)
                .setSmallIcon(R.drawable.mma_notification)
                .build();
        startForeground(1, notification.getNotification());//do heavy work on a background thread
        //stopSelf();
        return START_STICKY;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        Intent serviceIntent = new Intent(this, ForegroundService.class);
        serviceIntent.putExtra("inputExtra", "MMA is running in background.");
        ContextCompat.startForegroundService(this, serviceIntent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Intent serviceIntent = new Intent(this, ForegroundService.class);
        serviceIntent.putExtra("inputExtra", "MMA is running in background.");
        ContextCompat.startForegroundService(this, serviceIntent);
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }
}