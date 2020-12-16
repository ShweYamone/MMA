package com.freelance.solutionhub.mma.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.freelance.solutionhub.mma.DB.InitializeDatabase;
import com.freelance.solutionhub.mma.R;
import com.freelance.solutionhub.mma.adapter.DateTimePickerAdapter;
import com.freelance.solutionhub.mma.fragment.First_Step_PM_Fragment;
import com.freelance.solutionhub.mma.fragment.Second_Step_PM_Fragment;
import com.freelance.solutionhub.mma.model.Event;
import com.freelance.solutionhub.mma.model.PMServiceInfoDetailModel;
import com.freelance.solutionhub.mma.model.ReturnStatus;
import com.freelance.solutionhub.mma.model.UpdateEventBody;
import com.freelance.solutionhub.mma.util.ApiClient;
import com.freelance.solutionhub.mma.util.ApiInterface;
import com.freelance.solutionhub.mma.util.Network;
import com.freelance.solutionhub.mma.util.SharePreferenceHelper;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.freelance.solutionhub.mma.util.AppConstant.user_inactivity_time;

public class TelcoActivity extends AppCompatActivity implements View.OnClickListener{

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.et_telco_no)
    EditText telcoNo;

    @BindView(R.id.et_docker_no)
    EditText dockerNo;

    @BindView(R.id.et_ct_personnel)
    EditText ctPersonnel;

    @BindView(R.id.btn_expected_completion_date_time)
    Button expectedCompletionDateTime;

    @BindView(R.id.btn_clearance_date_time)
    Button clearanceDateTime;

    @BindView(R.id.btn_fault_detected_date_time)
    Button faultDetectedDateTime;

    @BindView(R.id.btn_action_date_time)
    Button actionDateTime;

    @BindView(R.id.et_action_taken)
    EditText actionTaken;

    @BindView(R.id.et_telco_officer)
    EditText telcoOfficer;

    @BindView(R.id.et_telco_fault_status)
    EditText telcoFaultStatus;

    @BindView(R.id.btn_refer_date_time)
    Button referDateTime;

    @BindView(R.id.btn_save)
    Button save;

    private SharePreferenceHelper mSharePreferenceHelper;
    private ApiInterface apiInterface;
    private Network network;
    private InitializeDatabase dbHelper;
    private ArrayList<Event> eventLists;
    private Date date;
    private String cmID;

    private Handler handler;
    private Runnable r;
    private boolean startHandler = true;
    private SharePreferenceHelper sharePreferenceHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_telco);
        ButterKnife.bind(this);
        sharePreferenceHelper = new SharePreferenceHelper(this);
        sharePreferenceHelper.setLock(false);

        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        eventLists = new ArrayList<>();
        apiInterface = ApiClient.getClient(this);
        mSharePreferenceHelper = new SharePreferenceHelper(this);
        network = new Network(this);
        dbHelper = InitializeDatabase.getInstance(this);
        date = new Date();
        setDateTimeButton();
        if(getIntent().hasExtra("id")){
            cmID = getIntent().getStringExtra("id");
        }else {
            finish();
        }
        Log.v("PMID",cmID);
        //Click
        referDateTime.setOnClickListener(this);
        expectedCompletionDateTime.setOnClickListener(this);
        clearanceDateTime.setOnClickListener(this);
        faultDetectedDateTime.setOnClickListener(this);
        actionDateTime.setOnClickListener(this);
        save.setOnClickListener(this);

        /**
         after certain amount of user inactivity, asks for passcode
         */
        handler = new Handler();
        r = new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
             //   Toast.makeText(MainActivity.this, "user is inactive from last 1 minute",Toast.LENGTH_SHORT).show();
                startHandler = false;
                Intent intent = new Intent(TelcoActivity.this, PasscodeActivity.class);
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
            sharePreferenceHelper.setLock(true);
    }

    @Override
    public void onUserInteraction() {
        // TODO Auto-generated method stub
        super.onUserInteraction();
        Toast.makeText(this, "UserInteraction", Toast.LENGTH_SHORT).show();
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
            Intent intent = new Intent(TelcoActivity.this, PasscodeActivity.class);
            intent.putExtra("workInMiddle", "work");
            startActivity(intent);
        } else {
            startHandler = true;
            startHandler();
        }
    }

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        sharePreferenceHelper.setLock(true);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_refer_date_time:
                new DateTimePickerAdapter(this,referDateTime).pickDateTime();
                break;
            case R.id.btn_expected_completion_date_time:
                new DateTimePickerAdapter(this,expectedCompletionDateTime).pickDateTime();
                break;
            case R.id.btn_clearance_date_time:
                new DateTimePickerAdapter(this, clearanceDateTime).pickDateTime();
                break;
            case R.id.btn_fault_detected_date_time:
                new DateTimePickerAdapter(this, faultDetectedDateTime).pickDateTime();
                break;
            case R.id.btn_action_date_time:
                new DateTimePickerAdapter(this,actionDateTime).pickDateTime();
                break;
            case R.id.btn_save:
                save();
                break;
        }
    }

    private void setDateTimeButton(){
        Timestamp timestamp = new Timestamp(date.getTime());
        String actualDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(timestamp);
        referDateTime.setText(actualDateTime);
        expectedCompletionDateTime.setText(actualDateTime);
        clearanceDateTime.setText(actualDateTime);
        faultDetectedDateTime.setText(actualDateTime);
        actionDateTime.setText(actualDateTime);

    }
    /**
     * Update event for all
     */
    public void save(){

        addEvents();
        Log.v("JOIN", eventLists.size() + "");

        date = new Date();
        Timestamp timestamp = new Timestamp(date.getTime());
        String actualDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(timestamp);

        UpdateEventBody updateEventBody;
        if (network.isNetworkAvailable()) {
            updateEventBody = new UpdateEventBody(mSharePreferenceHelper.getUserName(),
                    mSharePreferenceHelper.getUserId(),
                    actualDateTime,
                    cmID,
                    eventLists);
            Call<ReturnStatus> returnStatusCallEvent = apiInterface.updateEvent("Bearer " + mSharePreferenceHelper.getToken(), updateEventBody);
            returnStatusCallEvent.enqueue(new Callback<ReturnStatus>() {
                @Override
                public void onResponse(Call<ReturnStatus> call, Response<ReturnStatus> response) {
                    ReturnStatus returnStatus = response.body();
                    Log.v("SUCCESS","error");
                    if (response.isSuccessful()) {
                        Log.v("SUCCESS","success");
                        Toast.makeText(getApplicationContext(), returnStatus.getStatus() + "", Toast.LENGTH_LONG).show();

                    }
                }

                @Override
                public void onFailure(Call<ReturnStatus> call, Throwable t) {
                    Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            updateEventBody = new UpdateEventBody(mSharePreferenceHelper.getUserName(),
                    mSharePreferenceHelper.getUserId(),
                    actualDateTime,
                    cmID);
            String key = mSharePreferenceHelper.getUserId() + cmID + actualDateTime;
            updateEventBody.setId(key);
            dbHelper.updateEventBodyDAO().insert(updateEventBody);
            for (Event event: eventLists)
                event.setUpdateEventBodyKey(key);
            dbHelper.eventDAO().insertAll(eventLists);

        }


        eventLists.clear();
    }

    private void addEvents(){
        Log.v("TELCO","H"+telcoNo.getText().toString()+"H");
        if(!isEmpty(telcoNo))
            eventLists.add(new Event("TELCO_UPDATE","thirdPartyNumber", telcoNo.getText().toString()));
        if(!isEmpty(dockerNo))
            eventLists.add(new Event("TELCO_UPDATE", "docketNumber",dockerNo.getText().toString()));
        if(!isEmpty(ctPersonnel))
            eventLists.add(new Event("TELCO_UPDATE", "ctPersonnel",ctPersonnel.getText().toString()));
        eventLists.add(new Event("TELCO_UPDATE", "referDate",referDateTime.getText().toString()));
        eventLists.add(new Event("TELCO_UPDATE", "expectedCompletionDate",expectedCompletionDateTime.getText().toString()));
        eventLists.add(new Event("TELCO_UPDATE", "clearanceDate",clearanceDateTime.getText().toString()));
        if(!isEmpty(telcoFaultStatus))
            eventLists.add(new Event("TELCO_UPDATE", "faultStatus",telcoFaultStatus.getText().toString()));
        if(!isEmpty(telcoOfficer))
            eventLists.add(new Event("TELCO_UPDATE", "officer",telcoOfficer.getText().toString()));
        eventLists.add(new Event("TELCO_UPDATE", "faultDetectedDate",faultDetectedDateTime.getText().toString()));
        eventLists.add(new Event("TELCO_UPDATE", "actionDate",actionDateTime.getText().toString()));
        if(!isEmpty(actionTaken))
            eventLists.add(new Event("TELCO_UPDATE", "actionTaken",actionTaken.getText().toString()));
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private boolean isEmpty(EditText etText) {
        return etText.getText().toString().trim().length() == 0;
    }
}