package com.digisoft.mma.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.digisoft.mma.DB.InitializeDatabase;
import com.digisoft.mma.R;
import com.digisoft.mma.util.SharePreferenceHelper;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.digisoft.mma.util.AppConstant.user_inactivity_time;

public class PMCompletionActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.tvMSONumber)
    TextView tvMSONumber;

    @BindView(R.id.tv_panel_id)
    TextView tvPanelId;

    @BindView(R.id.tvSchedule)
    TextView tvSchedule;

    @BindView(R.id.tvScheduleType)
    TextView tvScheduleType;

    @BindView(R.id.tvStartDateTime)
    TextView tvStartDateTime;

    @BindView(R.id.tvEndDateTime)
    TextView tvEndDateTime;

    @BindView(R.id.tvRemarks)
    TextView tvRemarks;

    @BindView(R.id.btnClose)
    Button btnClose;

    private Handler handler;
    private Runnable r;
    private boolean startHandler = true;
    private boolean lockScreen = false;
    private SharePreferenceHelper sharePreferenceHelper;
    private InitializeDatabase dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pmcompletion);
        ButterKnife.bind(this);
        sharePreferenceHelper = new SharePreferenceHelper(this);
        dbHelper = InitializeDatabase.getInstance(this);
        sharePreferenceHelper.setLock(false);
        //Toast.makeText(getApplicationContext(), "mso start_time" + dbHelper.eventDAO().getEventValue("start_tag_in_time", "start_tag_in_time"),
        //        Toast.LENGTH_SHORT).show();
        tvMSONumber.setText(getIntent().getStringExtra("id"));
        tvPanelId.setText(dbHelper.eventDAO().getEventValue("panelId", "panelId"));
        tvSchedule.setText(dbHelper.eventDAO().getEventValue("schedule_date", "schedule_date"));
        tvScheduleType.setText(dbHelper.eventDAO().getEventValue("schedule_type", "schedule_type"));
        tvStartDateTime.setText(dbHelper.eventDAO().getEventValue("start_tag_in_time", "start_tag_in_time"));
        tvEndDateTime.setText(dbHelper.eventDAO().getEventValue("end_time", "end_time"));
        tvRemarks.setText(dbHelper.eventDAO().getEventValue("remarks", "remarks"));

        btnClose.setOnClickListener(this);

        /**
         after certain amount of user inactivity, asks for passcode
         */
        handler = new Handler();
        r = new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                Toast.makeText(PMCompletionActivity.this, "user is inactive from last 5 minutes",Toast.LENGTH_SHORT).show();
                startHandler = false;
                Intent intent = new Intent(PMCompletionActivity.this, PasscodeActivity.class);
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
        sharePreferenceHelper.setLock(true);
    }

    @Override
    public void onUserInteraction() {
        // TODO Auto-generated method stub
        super.onUserInteraction();
    //    Toast.makeText(this, "UserInteraction", Toast.LENGTH_SHORT).show();
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
            Intent intent = new Intent(PMCompletionActivity.this, PasscodeActivity.class);
            intent.putExtra("workInMiddle", "work");
            startActivity(intent);
        } else {
            startHandler = true;
            startHandler();
        }
    }

    @Override
    public void onClick(View view) {
        deleteWorkingData();
        Intent intent = new Intent(PMCompletionActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
     //   super.onBackPressed();
     //   sharePreferenceHelper.setLock(false);
    }

    private void deleteWorkingData() {
        dbHelper.updateEventBodyDAO().deleteAll();
        dbHelper.uploadPhotoDAO().deleteAll();
        dbHelper.eventDAO().deleteAll();
    }
}
