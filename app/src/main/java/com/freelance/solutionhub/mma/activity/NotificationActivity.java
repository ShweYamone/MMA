package com.freelance.solutionhub.mma.activity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
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
    private WebSocketUtils webSocketUtils;

    private SharePreferenceHelper mSharedPreference;
    private ApiInterface apiInterface;
    private String urlStr, channelStr;
    private Timestamp timestamp;
    private Date date;
    private SmartScrollListener mSmartScrollListener;
    private int page = 1;
    private int totalPages = 0 ;

    private static final String TAG = "NotificationActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        ButterKnife.bind(this);
        setupToolbar();
        apiInterface = ApiClient.getClient(this);
        mSharedPreference = new SharePreferenceHelper(this);

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
        webSocketUtils = new WebSocketUtils(this);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addOnScrollListener(mSmartScrollListener);
        recyclerView.setAdapter(mAdapter);
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
                            Log.i(TAG, "Joined with " + envelope.toString());
                            Log.i(TAG, "onMessage: " + socket.isConnected());
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

//                    Toast.makeText(getApplicationContext(), "NEW MESSAGE: " + envelope.toString(), Toast.LENGTH_SHORT).show();
                   //tvResult.setText("NEW MESSAGE: " + envelope.toString());
                    Log.i("NEW_MESSAGE",envelope.toString());
                   // tvMessage.setText(envelope.toString());
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

    private void getServiceOrders() {
        Call<NotificationReadModel> notificationReadModelCall = apiInterface.getNotificationReadList("Bearer "+mSharedPreference.getToken(),page,10);
        notificationReadModelCall.enqueue(new Callback<NotificationReadModel>() {
            @Override
            public void onResponse(Call<NotificationReadModel> call, Response<NotificationReadModel> response) {

                if(response.isSuccessful()){
                    NotificationReadModel readModel = response.body();
                    ArrayList<Item> items = new ArrayList<>();

                    items.addAll(readModel.getItems());
                    Toast.makeText(getApplicationContext(),"SUCCESS:"+items.size(),Toast.LENGTH_SHORT).show();
                    for(int i = 0;i<items.size();i++){
                        Payload payload = items.get(i).getPayload();
                        date = items.get(i).getInserted_at();
                        timestamp = new Timestamp(date.getTime());
                        String actualDateTime = new SimpleDateFormat("dd.MM.yyyy/HH:mm aa").format(timestamp);
                        if(payload.getMso_type().equals("PM")) {
                            notificationList.add(new NotificationModel("PM-MSO xxxx","You received an PM MSO Alert.",actualDateTime));
                        }else {
                            notificationList.add(new NotificationModel("CM-MSO xxxx","You received an CM MSO Alert.",actualDateTime));
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
        Call<NotificationReadModel> notificationReadModelCall = apiInterface.getNotificationReadList("Bearer "+mSharedPreference.getToken(),1,10);
        notificationReadModelCall.enqueue(new Callback<NotificationReadModel>() {
            @Override
            public void onResponse(Call<NotificationReadModel> call, Response<NotificationReadModel> response) {

                if(response.isSuccessful()){
                    NotificationReadModel readModel = response.body();
                    totalPages = readModel.getTotalPages();
                    ArrayList<Item> items = new ArrayList<>();
                    items.addAll(readModel.getItems());
                    Toast.makeText(getApplicationContext(),"SUCCESS:"+items.size(),Toast.LENGTH_SHORT).show();
                    for(int i = 0;i<items.size();i++){
                        Payload payload = items.get(i).getPayload();
                        date = items.get(i).getInserted_at();
                        timestamp = new Timestamp(date.getTime());
                        String actualDateTime = new SimpleDateFormat("dd.MM.yyyy/HH:mm aa").format(timestamp);
                        if(payload.getMso_type().equals("PM")) {
                            notificationList.add(new NotificationModel("PM-MSO xxxx","You received an PM MSO Alert.",actualDateTime));
                        }else {
                            notificationList.add(new NotificationModel("CM-MSO xxxx","You received an CM MSO Alert.",actualDateTime));
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
