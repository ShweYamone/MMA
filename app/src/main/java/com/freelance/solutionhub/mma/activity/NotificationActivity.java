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
    private WebSocketUtils webSocketUtils;

    private SharePreferenceHelper mSharedPreference;
    private String urlStr, channelStr;

    private static final String TAG = "NotificationActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        ButterKnife.bind(this);
        setupToolbar();
        mSharedPreference = new SharePreferenceHelper(this);
        mAdapter = new NotificationAdapter(this, notificationList);
        webSocketUtils = new WebSocketUtils(this);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(mAdapter);
        prepareNotifications();


        /************WebScoket***************/

        Uri.Builder url = Uri.parse( "ws://hub-nightly-public-alb-1826126491.ap-southeast-1.elb.amazonaws.com/socket/websocket" ).buildUpon();
        //   url.appendQueryParameter("vsn", "2.0.0");
        url.appendQueryParameter( "token", "eyJhbGciOiJSUzI1NiJ9.eyJyYW5kVXVpZCI6IjI0ZWU1MzE3ZWE0ZjQ3MmU4OGJmNzViMTkxNGFjYzZiIiwiYXVkIjpbImFwcC11c2VyIl0sImdyYW50X3R5cGUiOiJwYXNzd29yZCIsInVzZXJfbmFtZSI6Im1vYmlsZS0xIiwicGVybWlzc2lvbnMiOlsiRlVOQ19DUlVEX01NTV9DT05URU5UX19DT05UUkFDVE9SX1RBU0tTIiwiRlVOQ19WSUVXX01NTV9DT05URU5UX0NPTlRSQUNUT1JfVEFTS1MiXSwic2NvcGUiOlsiYXBwbGljYXRpb24iXSwiYXRpIjoiMmM0ODY1MWYtNTAyYi00N2E0LTlhOWMtMDc2MDE1OTE4MTQ2IiwiZXhwIjoxNjA3OTQ4OTYyLCJhdXRob3JpdGllcyI6WyJDT05UUkFDVE9SX01NTV9PRkZJQ0VSIiwiQ09OVFJBQ1RPUl9NTU1fVklFV0VSIl0sImp0aSI6ImJhNmMyN2VlLTZlNGQtNGUzNy04MWZjLWNiODNmM2U5ZmJmNiIsImNsaWVudF9pZCI6InRydXN0ZWQtYXBwIiwidXNlcm5hbWUiOiJtb2JpbGUtMSJ9.O7X8IWUi4PW23yXHzNOgErdAgQP_DxXYQSCQSR4o_F-DoFNH9UbGogyuqwpDkGvKE0SmZr5snZzar0mlTI6xZ2ynAueFJ7CUtUrk2NW8hyqjEJPQEo3cCaben2Oe1it5x8Cbb0QK3VbqccCDnUiUb1P7K5Vh6V3woYIyfivu1cKZUI2juhjnoahbqHSpfknVCrGHdgiqLdxYeG6T4YnXWxqbtSe6Ll0F5WJAg91pFNySkREdrPQpgNO0EXwThB4PG3B4bynuthb4MdAmoBq0b5ZkJ5elL6jNNQNsbhRwKzcO8illbtJMAy5vglvCuXJAzC_Xqgy_2tV4DkBBIKbtQw");
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
