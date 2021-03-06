package com.digisoft.mma.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.digisoft.mma.R;
import com.digisoft.mma.adapter.NotificationAdapter;
import com.digisoft.mma.common.SmartScrollListener;
import com.digisoft.mma.model.Item;
import com.digisoft.mma.model.NotificationModel;
import com.digisoft.mma.model.NotificationReadModel;
import com.digisoft.mma.model.Payload;
import com.digisoft.mma.util.ApiClientForNotification;
import com.digisoft.mma.util.ApiInterfaceForNotification;
import com.digisoft.mma.util.Network;
import com.digisoft.mma.util.SharePreferenceHelper;


import org.phoenixframework.channels.Channel;
import org.phoenixframework.channels.Envelope;
import org.phoenixframework.channels.IMessageCallback;
import org.phoenixframework.channels.Socket;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.digisoft.mma.util.AppConstant.ANNOUNCEMENT;
import static com.digisoft.mma.util.AppConstant.BEARER;
import static com.digisoft.mma.util.AppConstant.CM_MSO;
import static com.digisoft.mma.util.AppConstant.DATE_MONTH_YEAR;
import static com.digisoft.mma.util.AppConstant.PM_MSO;
import static com.digisoft.mma.util.AppConstant.RECEIVING_CM_ALERT;
import static com.digisoft.mma.util.AppConstant.RECEIVING_PM_ALERT;
import static com.digisoft.mma.util.AppConstant.UPLOAD_ERROR;
import static com.digisoft.mma.util.AppConstant.user_inactivity_time;

public class NotificationActivity extends AppCompatActivity  {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recyclerview_NotiList)
    RecyclerView recyclerView;
    @BindView(R.id.tvMessage)
    TextView tvMessage;

    @BindView(R.id.iv_no_internet)
    ImageView ivNoInternet;
    @BindView(R.id.tv_no_internet)
    TextView tvNoInternet;
    @BindView(R.id.layoutEmpty)
    LinearLayout llEmpty;

    private NotificationAdapter mAdapter;
    private List<NotificationModel> notificationList = new ArrayList<>();

    private Socket socket;
    private Channel channel;
    private SharePreferenceHelper mSharedPreference;
    private ApiInterfaceForNotification apiInterface;
    private Timestamp timestamp;
    private Date date;
    private SmartScrollListener mSmartScrollListener;
    private int page = 1;
    private int totalPages = 0 ;
    private Handler handler;
    private Runnable r;
    private boolean startHandler = true;
    private Network network;
    private final String mso_id = "mso_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        ButterKnife.bind(this);
        setupToolbar();

        network = new Network(this);
        if(network.isNetworkAvailable()) {
            apiInterface = ApiClientForNotification.getClient().create(ApiInterfaceForNotification.class);
            mSharedPreference = new SharePreferenceHelper(this);
            mSharedPreference.setLock(false);

            mSmartScrollListener = new SmartScrollListener(new SmartScrollListener.OnSmartScrollListener() {
                @Override
                public void onListEndReach() {

                    page++;
                    //Toast.makeText(getApplicationContext(), "Page " + page, Toast.LENGTH_SHORT).show();
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
        }else {
            recyclerView.setVisibility(View.GONE);
            tvNoInternet.setText("No internet connection!");
            ivNoInternet.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.icons_without_internet));
            llEmpty.setVisibility(View.VISIBLE);
        }
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
        if(network.isNetworkAvailable()) {
            if (mSharedPreference.getLock()) {
                Intent intent = new Intent(NotificationActivity.this, PasscodeActivity.class);
                intent.putExtra("workInMiddle", "work");
                startActivity(intent);
            } else {
                startHandler = true;
                startHandler();
            }

            /************WebScoket***************/
            Uri.Builder url = Uri.parse("ws://hub-nightly-public-alb-1826126491.ap-southeast-1.elb.amazonaws.com/socket/websocket").buildUpon();
            // url.appendQueryParameter("vsn", "2.0.0");
            url.appendQueryParameter("token", mSharedPreference.getToken());
            try {
                //    Log.i("Websocket", url.toString());
                socket = new Socket(url.build().toString());
                socket.connect();
                if (socket.isConnected()) {
                    Log.i("SOCKET_CONNECT", "SUCCESS");
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
                        Log.i("NEW_MESSAGE", envelope.toString());
                        final JsonNode user = envelope.getPayload().get(mso_id);
                        if (user == null || user instanceof NullNode) {
                            onMessageNoti("An anonymous user entered", "");
                        } else {
                            onMessageNoti(envelope.getPayload().get(mso_id) + "", "MSO is created.");
                        }

                    }
                });

                channel.on("mso_rejected", new IMessageCallback() {
                    @Override
                    public void onMessage(Envelope envelope) {
                        //  Toast.makeText(getApplicationContext(), "CLOSED: " + envelope.toString(), Toast.LENGTH_SHORT).show();
                        //   tvResult.setText("CLOSED: " + envelope.toString());
                        Log.i("CLOSED", envelope.toString());
                        final JsonNode user = envelope.getPayload().get(mso_id);
                        if (user == null || user instanceof NullNode) {
                            onMessageNoti("An anonymous user entered", "");
                        } else {
                            onMessageNoti(envelope.getPayload().get(mso_id) + "", "MSO is rejected.");
                        }
                    }
                });

            } catch (Exception e) {
         //       Toast.makeText(getApplicationContext(), "Exception" + e.getMessage(), Toast.LENGTH_SHORT).show();
                Log.i("Websocket", e.getMessage() + "\n" + e.getLocalizedMessage());
            }
        }
    }

    private void getServiceOrders() {
        Call<NotificationReadModel> notificationReadModelCall = apiInterface.getNotificationReadList(BEARER + mSharedPreference.getToken(),page,10);
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
                        String actualDateTime = new SimpleDateFormat(DATE_MONTH_YEAR).format(timestamp);
                        if(items.get(i).getType().equals("mso")) {
                            if (payload.getMso_type().equals("PM")) {
                                notificationList.add(new NotificationModel(items.get(i).getId(), PM_MSO + items.get(i).getPayload().getMso_id(), RECEIVING_PM_ALERT, actualDateTime, items.get(i).isIs_read()));
                            } else {
                                notificationList.add(new NotificationModel(items.get(i).getId(), CM_MSO + items.get(i).getPayload().getMso_id(), RECEIVING_CM_ALERT, actualDateTime, items.get(i).isIs_read()));
                            }
                        }else {
                            notificationList.add(new NotificationModel(items.get(i).getId(),ANNOUNCEMENT, payload.getText(),actualDateTime,items.get(i).isIs_read()));
                        }

                    }
                    mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<NotificationReadModel> call, Throwable t) {
                Log.i(UPLOAD_ERROR, "" + t.getLocalizedMessage());
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
    public void onMessageNoti(String envolope,String mso_type){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getMSOCreatedEvent();
            }
        });
    }

    private void getMSOCreatedEvent(){
        Call<NotificationReadModel> notificationReadModelCall = apiInterface.getNotificationReadList(BEARER + mSharedPreference.getToken(),1,1);
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
                        Log.i("NOTI",items.get(i).isIs_read()+":"+i);
                        Payload payload = items.get(i).getPayload();
                        date = items.get(i).getInserted_at();
                        timestamp = new Timestamp(date.getTime());
                        String actualDateTime = new SimpleDateFormat(DATE_MONTH_YEAR).format(timestamp);
                        if(items.get(i).getType().equals("mso")) {
                            if (payload.getMso_type().equals("PM")) {
                                notificationList.add(new NotificationModel(items.get(i).getId(), PM_MSO + items.get(i).getPayload().getMso_id(), RECEIVING_PM_ALERT, actualDateTime, items.get(i).isIs_read()));
                            } else {
                                notificationList.add(new NotificationModel(items.get(i).getId(), CM_MSO + items.get(i).getPayload().getMso_id(), RECEIVING_CM_ALERT, actualDateTime, items.get(i).isIs_read()));
                            }
                        }else {
                            notificationList.add(new NotificationModel(items.get(i).getId(),ANNOUNCEMENT, payload.getText(),actualDateTime,items.get(i).isIs_read()));
                        }

                    }
                    mAdapter.notifyItemInserted(0);
                }
            }

            @Override
            public void onFailure(Call<NotificationReadModel> call, Throwable t) {
                Log.i(UPLOAD_ERROR, "" + t.getLocalizedMessage());
            }
        });
    }

    private void getMSOEvent(){
        Call<NotificationReadModel> notificationReadModelCall = apiInterface.getNotificationReadList(BEARER + mSharedPreference.getToken(),1,10);
        notificationReadModelCall.enqueue(new Callback<NotificationReadModel>() {
            @Override
            public void onResponse(Call<NotificationReadModel> call, Response<NotificationReadModel> response) {

                if(response.isSuccessful()){
                    NotificationReadModel readModel = response.body();
                  //  totalPages = readModel.getTotalPages();
                    totalPages = 10;
                    ArrayList<Item> items = new ArrayList<>();
                    items.addAll(readModel.getItems());
                    if(items.size() == 0){
                        recyclerView.setVisibility(View.GONE);
                        tvNoInternet.setText("No notification!");
                        ivNoInternet.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.bell));
                        llEmpty.setVisibility(View.VISIBLE);
                    }
                 //   Toast.makeText(getApplicationContext(),"SUCCESS:"+items.size(),Toast.LENGTH_SHORT).show();
                    for(int i = 0;i<items.size();i++){
                        Log.i("Is_read",items.get(i).isIs_read()+":"+i);
                        Payload payload = items.get(i).getPayload();
                        date = items.get(i).getInserted_at();
                        timestamp = new Timestamp(date.getTime());
                        String actualDateTime = new SimpleDateFormat(DATE_MONTH_YEAR).format(timestamp);
                        if(items.get(i).getType().equals("mso")) {
                            if (payload.getMso_type().equals("PM")) {
                                notificationList.add(new NotificationModel(items.get(i).getId(), PM_MSO + items.get(i).getPayload().getMso_id(), RECEIVING_PM_ALERT, actualDateTime, items.get(i).isIs_read()));
                            } else {
                                notificationList.add(new NotificationModel(items.get(i).getId(), CM_MSO + items.get(i).getPayload().getMso_id(), RECEIVING_CM_ALERT, actualDateTime, items.get(i).isIs_read()));
                            }
                        }else {
                            notificationList.add(new NotificationModel(items.get(i).getId(),ANNOUNCEMENT, payload.getText(),actualDateTime,items.get(i).isIs_read()));
                        }

                    }
                    mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<NotificationReadModel> call, Throwable t) {
                    Log.i(UPLOAD_ERROR , "" + t.getLocalizedMessage());
            }
        });
    }

    //For Back Arrow
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mSharedPreference.setLock(false);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mSharedPreference.setLock(false);
        finish();
    }
}
