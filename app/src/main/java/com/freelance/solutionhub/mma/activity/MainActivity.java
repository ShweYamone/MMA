package com.freelance.solutionhub.mma.activity;


import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.freelance.solutionhub.mma.R;
import com.freelance.solutionhub.mma.fragment.AboutFragment;
import com.freelance.solutionhub.mma.fragment.HomeFragment;
import com.freelance.solutionhub.mma.model.Item;
import com.freelance.solutionhub.mma.model.NotificationReadModel;
import com.freelance.solutionhub.mma.util.ApiClient;
import com.freelance.solutionhub.mma.util.ApiClientForNotification;
import com.freelance.solutionhub.mma.util.ApiInterface;
import com.freelance.solutionhub.mma.util.ApiInterfaceForNotification;
import com.freelance.solutionhub.mma.util.SharePreferenceHelper;
import com.freelance.solutionhub.mma.util.WebSocketUtils;
import com.google.android.material.navigation.NavigationView;

import org.phoenixframework.channels.Channel;
import org.phoenixframework.channels.Envelope;
import org.phoenixframework.channels.IMessageCallback;
import org.phoenixframework.channels.Socket;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.freelance.solutionhub.mma.util.AppConstant.user_inactivity_time;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

    @BindView(R.id.navigation_view)
    NavigationView mNavigationView;

    @BindView(R.id.tvLogout)
    TextView tvLogout;

    private int count;
    protected ActionBarDrawerToggle mDrawerToggle;
    private SharePreferenceHelper mSharedPreferences;
    private Runnable runnable;
    private int passcode;
    private ApiInterface apiInterface;
    private ApiInterfaceForNotification apiInterfaceForNotification;
    private Handler handler;
    private Runnable r;
    private boolean startHandler = true;
    private boolean lockScreen = false;
    private Socket socket;
    private Channel channel;
    private WebSocketUtils webSocketUtils;
    private Menu menu;
    private RelativeLayout notificationRelativeLayout;
    private TextView notificationCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSharedPreferences = new SharePreferenceHelper(this);
        mSharedPreferences.setLock(false);

        ButterKnife.bind(this);
        setupToolbar();
        initNavigationDrawer();

        displayView(R.id.nav_home);
        // lotout txtview is here
        tvLogout.setOnClickListener(this);
        apiInterface = ApiClient.getClient(this);
        apiInterfaceForNotification = ApiClientForNotification.getClient().create(ApiInterfaceForNotification.class);
        webSocketUtils = new WebSocketUtils(this);

        /**
         * call service
         */
       // startService(new Intent(getBaseContext(), OnClearFromRecentService.class));

        /**
        after certain amount of user inactivity, asks for passcode
         */
        handler = new Handler();
        r = new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                Toast.makeText(MainActivity.this, "user is inactive from last 5 minutes",Toast.LENGTH_SHORT).show();
                startHandler = false;
                Intent intent = new Intent(MainActivity.this, PasscodeActivity.class);
                intent.putExtra("workInMiddle", "work");
                startActivity(intent);
                stopHandler();
            }
        };
       // startHandler();
    }

    @Override
    public void onClick(View view) {
        mSharedPreferences.logoutSharePreference();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        boolean isScreenOn = pm.isInteractive();
        if (isScreenOn)
            stopHandler();
        else
            mSharedPreferences.setLock(true);
    }

    @Override
    public void onUserInteraction() {
        // TODO Auto-generated method stub
        super.onUserInteraction();
       // Toast.makeText(this, "UserInteraction", Toast.LENGTH_SHORT).show();
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
        Log.i("LOCKSCREEN", "onResume: " + mSharedPreferences.getLock());
        if (mSharedPreferences.getLock()) {
            Intent intent = new Intent(MainActivity.this, PasscodeActivity.class);
            intent.putExtra("workInMiddle", "work");
            startActivity(intent);
        } else {
            startHandler = true;
            startHandler();
        }
        if(menu != null)
            getMSOEvent();

        /************WebScoket***************/
        Uri.Builder url = Uri.parse( "ws://hub-nightly-public-alb-1826126491.ap-southeast-1.elb.amazonaws.com/socket/websocket" ).buildUpon();
        // url.appendQueryParameter("vsn", "2.0.0");
        url.appendQueryParameter( "token", mSharedPreferences.getToken());
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
                        onMessageNoti("An anonymous user entered","");
                    }
                    else {
                        onMessageNoti(envelope.getPayload().get("mso_id")+"","MSO is created.");
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
                        onMessageNoti(envelope.getPayload().get("mso_id")+"","MSO is rejected.");
                    }
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
        Log.i("LOCKSCREENUserLeave", "onResume: " + mSharedPreferences.getLock());
        mSharedPreferences.setLock(true);
    }

    private void setupToolbar() {

        setSupportActionBar(toolbar);
        final ActionBar ab = getSupportActionBar();
        ab.setDisplayShowTitleEnabled(false);
        //ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);
    }
    public void onMessageNoti(String envolope,String mso_type){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                sendNotification(envolope,mso_type);
                if(menu != null)
                    menu.getItem(0).setIcon(buildCounterDrawable(count+1,R.drawable.bell));
            }
        });
    }
    //  @OnClick(R.id.button)
    public void sendNotification(String title, String type) {

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String NOTIFICATION_CHANNEL_ID = "tutorialspoint_01";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            @SuppressLint("WrongConstant") NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "My Notifications", NotificationManager.IMPORTANCE_MAX);
            // Configure the notification channel.
            notificationChannel.setDescription("Sample Channel description");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.enableVibration(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }
        Intent intent = new Intent(this, NotificationActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        notificationBuilder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(type)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(type))
                .setContentInfo("Information")
                .setContentIntent(pendingIntent);
        notificationManager.notify(1, notificationBuilder.build());
    }


    private void initNavigationDrawer() {

        mDrawerLayout.setScrimColor(ContextCompat.getColor(getApplicationContext(), android.R.color.transparent));
        setupActionBarDrawerToogle();
        if (mNavigationView != null) {
            setupDrawerContent(mNavigationView);
            Menu menu = mNavigationView.getMenu();
            MenuItem menuItem = menu.findItem(R.id.nav_home);
            menuItem.setTitle(mSharedPreferences.getUserName());
        }

        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });
    }

    /**
     * In case if you require to handle drawer open and close states
     */
    private void setupActionBarDrawerToogle() {

        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.string.drawer_open,  /* "open drawer" description */
                R.string.drawer_close  /* "close drawer" description */
        ) {

            /**
             * Called when a drawer has settled in a completely closed state.
             */
            public void onDrawerClosed(View view) {
                //Snackbar.make(view, R.string.drawer_close, Snackbar.LENGTH_SHORT).show();
            }

            /**
             * Called when a drawer has settled in a completely open state.
             */
            public void onDrawerOpened(View drawerView) {
                //Snackbar.make(drawerView, R.string.drawer_open, Snackbar.LENGTH_SHORT).show();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

    }

    private void setupDrawerContent(NavigationView navigationView) {

        //setting up selected item listener
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        menuItem.setChecked(true);

        Log.i("CLICK ", menuItem.getItemId()+"");
        displayView(menuItem.getItemId());

        mDrawerLayout.closeDrawers();
        return true;
    }


    private void displayView(int menuid) {
        Fragment fragment = null;
        String title = getString(R.string.app_name);
        switch (menuid) {
            case R.id.nav_home:
                fragment = new HomeFragment();
                title = getString(R.string.nav_home);
                break;
            case R.id.nav_asset_information:
                Intent intent = new Intent(MainActivity.this, AssetInformationActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_setting:
                fragment = new AboutFragment();
            default:
                break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_body, fragment);
            fragmentTransaction.commit();

            // set the toolbar title
            getSupportActionBar().setTitle(title);
        }
    }

    @Override
    public void onBackPressed() {
        if (isNavDrawerOpen()) {
            closeNavDrawer();
        } else {
            super.onBackPressed();
        }
    }

    protected boolean isNavDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(GravityCompat.START);
    }

    protected void closeNavDrawer() {
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_main, menu);
        getMSOEvent();
        return true;
    }
    private Drawable buildCounterDrawable(int count, int backgroundImageId) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.notification_layout, null);
        view.setBackgroundResource(backgroundImageId);

        if (count == 0) {
            View counterTextPanel = view.findViewById(R.id.counterValuePanel);
            counterTextPanel.setVisibility(View.GONE);
        } else {
            TextView textView = (TextView) view.findViewById(R.id.count);
            textView.setText("" + count);
        }

        view.measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());

        view.setDrawingCacheEnabled(true);
        view.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);

        return new BitmapDrawable(getResources(), bitmap);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);  // OPEN DRAWER
                return true;

            case R.id.action_notifications:
                Intent intent = new Intent(this, NotificationActivity.class);
                startActivity(intent);
                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    private void getMSOEvent(){
        count = 0;
       Call<NotificationReadModel> notificationReadModelCall = apiInterfaceForNotification.getNotificationReadList("Bearer "+mSharedPreferences.getToken(),1,10);
        notificationReadModelCall.enqueue(new Callback<NotificationReadModel>() {
            @Override
            public void onResponse(Call<NotificationReadModel> call, Response<NotificationReadModel> response) {

                NotificationReadModel readModel = response.body();
                if(response.isSuccessful()){
                    List<Item> items = readModel.getItems();
                    Toast.makeText(getApplicationContext(),"SUCCESS:"+items.size(),Toast.LENGTH_SHORT).show();
                    for(int i = 0;i<items.size();i++){
                        Log.i("Is_read_Main",items.get(i).isIs_read()+":"+i);
                        if(!items.get(i).isIs_read()){
                            count++;
                        }
                    }
                    if(menu != null)
                        menu.getItem(0).setIcon(buildCounterDrawable(count,R.drawable.bell));
                }
            }

            @Override
            public void onFailure(Call<NotificationReadModel> call, Throwable t) {
                Log.i("ERROR",t.getLocalizedMessage());
            }
        });


    }
    /**
     * Encode photo string to decode string
     */
    private String uploadPhotoByDecoding(String s){
        byte[] data = Base64.decode(s,Base64.DEFAULT);
        String s1 = new String(data);
        Log.v("DECODE", s1);
        return s1;
    }
}
