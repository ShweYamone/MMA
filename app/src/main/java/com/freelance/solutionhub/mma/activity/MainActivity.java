package com.freelance.solutionhub.mma.activity;


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
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
import com.freelance.solutionhub.mma.util.SharePreferenceHelper;
import com.freelance.solutionhub.mma.util.WebSocketUtils;
import com.google.android.material.navigation.NavigationView;

import org.phoenixframework.channels.Channel;
import org.phoenixframework.channels.Envelope;
import org.phoenixframework.channels.IMessageCallback;
import org.phoenixframework.channels.Socket;

import butterknife.BindView;
import butterknife.ButterKnife;

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

    protected ActionBarDrawerToggle mDrawerToggle;
    private SharePreferenceHelper mSharedPreferences;
    private Runnable runnable;
    private int passcode;
    private Handler handler;
    private Runnable r;
    private boolean startHandler = true;
    private boolean lockScreen = false;
    private Socket socket;
    private Channel channel;
    private WebSocketUtils webSocketUtils;
    private Menu menu;

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
                Toast.makeText(MainActivity.this, "user is inactive from last 1 minute",Toast.LENGTH_SHORT).show();
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
                    final JsonNode user = envelope.getPayload().get("user");
                    if (user == null || user instanceof NullNode) {
                        onMessageToast("An anonymous user entered");
                    }
                    else {
                        onMessageToast("User '" + user.toString() + "' entered");
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
    public void onMessageToast(String envolope){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                menu.getItem(0).setIcon(ContextCompat.getDrawable(MainActivity.this, R.drawable.noti_new));
                sendNotification();
            }
        });
    }
    //  @OnClick(R.id.button)
    public void sendNotification() {

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
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        notificationBuilder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setTicker("Tutorialspoint")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("Much longer text that cannot fit one line..."))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentTitle("sample notification")
                .setContentText("This is sample notification")
                .setContentInfo("Information");
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
        getMenuInflater().inflate(R.menu.menu_main, menu);
        this.menu = menu;
        return true;
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
