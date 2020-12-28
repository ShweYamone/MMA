package com.freelance.solutionhub.mma.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.freelance.solutionhub.mma.DB.InitializeDatabase;
import com.freelance.solutionhub.mma.R;
import com.freelance.solutionhub.mma.model.PMServiceInfoDetailModel;
import com.freelance.solutionhub.mma.model.ServiceInfoModel;
import com.freelance.solutionhub.mma.util.SharePreferenceHelper;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.freelance.solutionhub.mma.util.AppConstant.FAULT_PART_CODE;
import static com.freelance.solutionhub.mma.util.AppConstant.OTHER_CONTRACTOR_UPDATE;
import static com.freelance.solutionhub.mma.util.AppConstant.POWER_GRIP_UPDATE;
import static com.freelance.solutionhub.mma.util.AppConstant.REMEDY;
import static com.freelance.solutionhub.mma.util.AppConstant.REPLACEMENT_PART_CODE;
import static com.freelance.solutionhub.mma.util.AppConstant.REPORTED_PROBLEM;
import static com.freelance.solutionhub.mma.util.AppConstant.SERVICE_ORDER_UPDATE;
import static com.freelance.solutionhub.mma.util.AppConstant.TELCO_UPDATE;
import static com.freelance.solutionhub.mma.util.AppConstant.user_inactivity_time;

public class CMCompletionActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.tvRemarks)
    TextView tvRemarks;

    @BindView(R.id.tv_mso_number)
    TextView msoNumber;

    @BindView(R.id.tv_panel_id)
    TextView panelId;

    @BindView(R.id.tv_reported_problem_code)
    TextView reportedProblemCode;

    @BindView(R.id.tv_fault_code)
    TextView faultCode;

    @BindView(R.id.tv_remedy_action)
    TextView remedyAction;

    @BindView(R.id.tv_third_party_fault)
    TextView thirdPartyFault;

    @BindView(R.id.tv_location)
    TextView location;

    @BindView(R.id.btnClose)
    Button btnClose;

    private Runnable r;
    private Handler handler;
    private boolean startHandler = true;
    private SharePreferenceHelper sharePreferenceHelper;
    private InitializeDatabase dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_cmcompletion);
        ButterKnife.bind(this);
        sharePreferenceHelper = new SharePreferenceHelper(this);
        dbHelper = InitializeDatabase.getInstance(this);
        sharePreferenceHelper.setLock(false);

        PMServiceInfoDetailModel serviceInfoModel = (PMServiceInfoDetailModel)getIntent().getSerializableExtra("object");

        msoNumber.setText(getIntent().getStringExtra("id"));
        panelId.setText(dbHelper.eventDAO().getEventValue("panelId" , "panelId"));
        reportedProblemCode.setText(dbHelper.eventDAO().getEventValue("reported", "reported"));
        faultCode.setText(dbHelper.eventDAO().getEventValue("faultpartcode", "faultpartcode"));
        remedyAction.setText(dbHelper.eventDAO().getEventValue("remedy", "remedy"));

        if (dbHelper.eventDAO().getNumOfEventsByEventType(TELCO_UPDATE) > 0) {
            thirdPartyFault.setText("Telco");
        } else if (dbHelper.eventDAO().getNumOfEventsByEventType(POWER_GRIP_UPDATE) > 0) {
            thirdPartyFault.setText("POWER GRIP");
        } else if (dbHelper.eventDAO().getNumOfEventsByEventType(OTHER_CONTRACTOR_UPDATE) > 0)
            thirdPartyFault.setText("Other Contractor");
        else
            thirdPartyFault.setText("");

        location.setText(dbHelper.eventDAO().getEventValue("location", "location"));
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
                Toast.makeText(CMCompletionActivity.this, "user is inactive from last 5 minutes",Toast.LENGTH_SHORT).show();
                startHandler = false;
                Intent intent = new Intent(CMCompletionActivity.this, PasscodeActivity.class);
                intent.putExtra("workInMiddle", "work");
                startActivity(intent);
                stopHandler();
            }
        };

    }

    private void deleteWorkingData() {
        dbHelper.updateEventBodyDAO().deleteAll();
        dbHelper.uploadPhotoDAO().deleteAll();
        dbHelper.eventDAO().deleteAll();
        dbHelper.checkListDescDAO().deleteAll();
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
        if (sharePreferenceHelper.getLock()) {
            Intent intent = new Intent(CMCompletionActivity.this, PasscodeActivity.class);
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnClose:
                deleteWorkingData();
                Intent intent = new Intent(CMCompletionActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
        }
    }

}
