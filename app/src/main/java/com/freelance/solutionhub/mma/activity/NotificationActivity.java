package com.freelance.solutionhub.mma.activity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
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
import com.freelance.solutionhub.mma.adapter.NotificationAdapter;
import com.freelance.solutionhub.mma.common.SmartScrollListener;
import com.freelance.solutionhub.mma.delegate.HomeFragmentCallback;
import com.freelance.solutionhub.mma.model.FilterModelBody;
import com.freelance.solutionhub.mma.model.Item;
import com.freelance.solutionhub.mma.model.NotificationModel;
import com.freelance.solutionhub.mma.model.NotificationReadModel;
import com.freelance.solutionhub.mma.model.PMServiceListModel;
import com.freelance.solutionhub.mma.model.Payload;
import com.freelance.solutionhub.mma.util.ApiClient;
import com.freelance.solutionhub.mma.util.ApiInterface;
import com.freelance.solutionhub.mma.util.SharePreferenceHelper;
import com.freelance.solutionhub.mma.util.WebSocketUtils;


import org.phoenixframework.channels.Channel;
import org.phoenixframework.channels.ChannelEvent;
import org.phoenixframework.channels.Envelope;
import org.phoenixframework.channels.IMessageCallback;
import org.phoenixframework.channels.Socket;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.freelance.solutionhub.mma.util.AppConstant.user_inactivity_time;

public class NotificationActivity extends AppCompatActivity  {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recyclerview_NotiList)
    RecyclerView recyclerView;
    @BindView(R.id.tvMessage)
    TextView tvMessage;

    private NotificationAdapter mAdapter;
    private List<NotificationModel> notificationList = new ArrayList<>();

    private Socket socket;
    private Channel channel;
    private SharePreferenceHelper mSharedPreference;
    private ApiInterface apiInterface;
    private String urlStr, channelStr;
    private Timestamp timestamp;
    private Date date;
    private SmartScrollListener mSmartScrollListener;
    private int page = 1;
    private int totalPages = 0 ;
    private Handler handler;
    private Runnable r;
    private boolean startHandler = true;
    private boolean lockScreen = false;

    private static final String TAG = "NotificationActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        ButterKnife.bind(this);
        setupToolbar();
        apiInterface = ApiClient.getClient(this);
        mSharedPreference = new SharePreferenceHelper(this);
        mSharedPreference.setLock(false);

        mSmartScrollListener = new SmartScrollListener(new SmartScrollListener.OnSmartScrollListener() {
            @Override
            public void onListEndReach() {

                page++;
                Toast.makeText(getApplicationContext(), "Page " + page, Toast.LENGTH_SHORT).show();
                if (page <= totalPages)
                    getServiceOrders();
            }
        });


        mAdapter = new NotificationAdapter(this, notificationList);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addOnScrollListener(mSmartScrollListener);
        recyclerView.setAdapter(mAdapter);


        getMSOEvent();
        /**
         after certain amount of user inactivity, asks for passcode
         */
        handler = new Handler();
        r = new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                Toast.makeText(NotificationActivity.this, "user is inactive from last 5 minutes",Toast.LENGTH_SHORT).show();
                startHandler = false;
                Intent intent = new Intent(NotificationActivity.this, PasscodeActivity.class);
                intent.putExtra("workInMiddle", "work");
                startActivity(intent);
                stopHandler();
            }
        };
    }



    @Override
    protected void onStop() {
        super.onStop();
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        boolean isScreenOn = pm.isInteractive();
        if (isScreenOn)
            stopHandler();
        else
            mSharedPreference.setLock(true);
    }

    @Override
    public void onUserInteraction() {
        // TODO Auto-generated method stub
        super.onUserInteraction();
     //   Toast.makeText(this, "UserInteraction", Toast.LENGTH_SHORT).show();
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
        if (mSharedPreference.getLock()) {
            Intent intent = new Intent(NotificationActivity.this, PasscodeActivity.class);
            intent.putExtra("workInMiddle", "work");
            startActivity(intent);
        } else {
            startHandler = true;
            startHandler();
        }
//        if(notificationList.size()>=11){
//            Log.i("LIST10","WORK");
//            for(int i = 1,j=notificationList.size()-1;i<11;i++,j--){
//                notificationList.remove(j);
//            }
//            getServiceOrders();
//        }else {
//            Log.i("LIST","WORK");
//
//        }
        page = 1;
        notificationList.clear();
        mAdapter.notifyDataSetChanged();
        getMSOEvent();

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
                    }
                    else {
                    }

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
    }

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        mSharedPreference.setLock(true);
    }


    private void getServiceOrders() {
        Call<NotificationReadModel> notificationReadModelCall = apiInterface.getNotificationReadList("Bearer "+mSharedPreference.getToken(),page,5);
        notificationReadModelCall.enqueue(new Callback<NotificationReadModel>() {
            @Override
            public void onResponse(Call<NotificationReadModel> call, Response<NotificationReadModel> response) {

                if(response.isSuccessful()){
                    NotificationReadModel readModel = response.body();
                    ArrayList<Item> items = new ArrayList<>();

                    items.addAll(readModel.getItems());
                 //   Toast.makeText(getApplicationContext(),"SUCCESS:"+items.size(),Toast.LENGTH_SHORT).show();
                    for(int i = 0;i<items.size();i++){
                        Log.i("IS_READ",""+items.get(i).isIs_read());
                        Payload payload = items.get(i).getPayload();
                        date = items.get(i).getInserted_at();
                        timestamp = new Timestamp(date.getTime());
                        String actualDateTime = new SimpleDateFormat("dd.MM.yyyy/HH:mm aa").format(timestamp);
                        if(payload.getMso_type().equals("PM")) {
                            notificationList.add(new NotificationModel(items.get(i).getId(),"PM-MSO xxxx","You received an PM MSO Alert.",actualDateTime,items.get(i).isIs_read()));
                        }else {
                            notificationList.add(new NotificationModel(items.get(i).getId(),"CM-MSO xxxx","You received an CM MSO Alert.",actualDateTime, items.get(i).isIs_read()));
                        }

                    }
                    mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<NotificationReadModel> call, Throwable t) {
                Log.i("ERROR",t.getLocalizedMessage());
            }
        });

    }


    private void setupToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        final ActionBar ab = getSupportActionBar();
        //ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);
    }

    private void getMSOEvent(){
        Call<NotificationReadModel> notificationReadModelCall = apiInterface.getNotificationReadList("Bearer "+mSharedPreference.getToken(),1,5);
        notificationReadModelCall.enqueue(new Callback<NotificationReadModel>() {
            @Override
            public void onResponse(Call<NotificationReadModel> call, Response<NotificationReadModel> response) {

                if(response.isSuccessful()){
                    NotificationReadModel readModel = response.body();
                  //  totalPages = readModel.getTotalPages();
                    totalPages = 10;
                    ArrayList<Item> items = new ArrayList<>();
                    items.addAll(readModel.getItems());
                 //   Toast.makeText(getApplicationContext(),"SUCCESS:"+items.size(),Toast.LENGTH_SHORT).show();
                    for(int i = 0;i<items.size();i++){
                        Log.i("Is_read",items.get(i).isIs_read()+":"+i);
                        Payload payload = items.get(i).getPayload();
                        date = items.get(i).getInserted_at();
                        timestamp = new Timestamp(date.getTime());
                        String actualDateTime = new SimpleDateFormat("dd.MM.yyyy/HH:mm aa").format(timestamp);
                        if(payload.getMso_type().equals("PM")) {
                            notificationList.add(new NotificationModel(items.get(i).getId(),"PM-MSO xxxx","You received an PM MSO Alert.",actualDateTime,items.get(i).isIs_read()));
                        }else {
                            notificationList.add(new NotificationModel(items.get(i).getId(),"CM-MSO xxxx","You received an CM MSO Alert.",actualDateTime,items.get(i).isIs_read()));
                        }

                    }
                    mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<NotificationReadModel> call, Throwable t) {
                    Log.i("ERROR",t.getLocalizedMessage());
            }
        });
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
    public void onBackPressed(){
        finish();
    }
}
